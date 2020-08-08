module V1
  module Authenticated
    class User < Grape::API
      namespace :user do
        desc 'update profile'
        params do
          requires :email, type: String, allow_blank: false
          requires :name, type: String, allow_blank: true
          requires :username, type: String, allow_blank: false
          requires :phone_number, type: String, allow_blank: true
        end
        put '/update' do
          if current_user.update(permitted_params)
            render_success(message: 'User Updated', data: serialized_data(current_user))
          else
            render_error(message: current_user.errors.full_messages.join(', '))
          end
        end

        desc 'update password'
        params do
          optional :password, type: String, allow_blank: false
          optional :new_password, type: String, allow_blank: false
        end
        put '/update_pass' do
          if !current_user.valid_password?(permitted_params[:password])
            render_error(message: 'Invalid password')
          elsif current_user.update(password: params[:new_password])
            render_success(message: 'Password Updated', data: serialized_data(current_user))
          else
            render_error(message: current_user.errors.full_messages.join(', '))
          end
        end
      end
    end
  end
end
