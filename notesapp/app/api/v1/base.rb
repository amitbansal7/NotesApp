module V1
  class Base < Grape::API
    version 'v1', using: :path

    helpers V1::Helpers::Utils

    helpers do
      def permitted_params
        @permitted_params ||= declared(params, include_mission: false)
      end
    end

    mount V1::Open::Base
    mount V1::Authenticated::Base
  end
end
