module V1
  module Helpers
    module NotesHelper
      extend Grape::API::Helpers

      def apply_filters
        params[:filters].to_a.each do |key, value|
          case key.to_sym
          when :id
            @notes = @notes.where(id: value)
          end
        end
      end
    end
  end
end
