module V1
  module Authenticated
    class Notes < Grape::API
      helpers V1::Helpers::NotesHelper
      helpers V1::Helpers::Utils

      namespace :notes do
        desc 'Get all notes'
        params do
          use :pagination
          optional :filters, type: Hash do
            optional :id, type: Integer
          end
        end
        get '' do
          @notes = current_user.notes
          apply_filters

          data = @notes.order(updated_at: :desc)
                       .offset(params[:per_page] * (params[:page] - 1))
                       .limit(params[:per_page])
                       .includes(:user).map do |note|
            serialized_data(note)
          end
          render_success(message: 'Notes', data: data)
        end

        desc 'Create a note'
        params do
          requires :title, type: String, allow_blank: false
          optional :text, type: String, allow_blank: false
        end
        post '' do
          note = current_user.notes.create(permitted_params)
          if note.save
            render_success(message: 'Note created', data: serialized_data(note))
          else
            render_error(message: note.errors.full_messages.join(', '))
         end
        end

        desc 'Update a note'
        params do
          requires :title, type: String, allow_blank: false
          optional :text, type: String, allow_blank: false
        end
        put ':id' do
          note = current_user.notes.find(params[:id])
          if note.update(permitted_params)
            render_success(message: 'Note Updated', data: serialized_data(note))
          else
            render_error(message: note.errors.full_messages.join(', '))
         end
        end

        desc 'make public'
        params do
        end
        put 'public/:id' do
          note = current_user.notes.find(params[:id])
          note.make_public
          render_success(message: 'Note is Public now', data: serialized_data(note))
        end

        desc 'make private'
        params do
        end
        put 'private/:id' do
          note = current_user.notes.find(params[:id])
          note.make_private
          render_success(message: 'Note is Private now', data: serialized_data(note))
        end

        desc 'delete a note'
        params do
        end
        delete ':id' do
          note = current_user.notes.find(params[:id])
          if note.destroy
            render_success(message: 'Note deleted')
          else
            render_error(message: note.errors.full_messages.join(', '))
          end
        end
      end
    end
  end
end
