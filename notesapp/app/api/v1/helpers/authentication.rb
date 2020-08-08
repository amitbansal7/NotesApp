module V1
  module Helpers
    module Authentication
      extend Grape::API::Helpers

      def current_user
        @current_user ||= User.find_by(auth_token: auth_token)
      end

      def authenticate!
        error!('401 Unauthorized', 401) unless current_user.present?
      end

      def auth_token
        headers["Authentication"]
      end
    end
  end
end
