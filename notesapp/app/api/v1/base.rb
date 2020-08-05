module V1
  class Base < Grape::API
    version 'v1', using: :path

    helpers V1::Helpers::Utils

    mount V1::Open::Base
    mount V1::Users::Base

    helpers do
      def permitted_params
        @permitted_params ||= declared(params, include_mission: false)
      end
    end

    rescue_from ActiveRecord::RecordNotFound do
      error!({ message: 'Not Found' }, 404)
    end
    rescue_from ActionController::RoutingError do
      error!({ message: 'Not Found' }, 404)
    end
  end
end
