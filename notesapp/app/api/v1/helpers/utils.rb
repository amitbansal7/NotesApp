module V1
  module Helpers
    module Utils
      extend Grape::API::Helpers

      def render_error(code: 422, message: '', data: {})
        status code
        { message: message, data: data }
      end

      def render_success(code: 200, message: '', data: {})
        status code
        { message: message, data: data }
      end

      def deny_access
        error!({ message: 'Not Authorized' }, 401)
      end

      def rack_code(symbol)
        Rack::Utils::SYMBOL_TO_STATUS_CODE[symbol.to_sym]
      end

      def user_json(user)
        return {} unless user

        UserSerializer.new(user).serializable_hash.dig(:data, :attributes)
      end

      params :pagination do
        optional :page, type: Integer, default: 1, allow_blank: false, values: ->(v) { v > 0 }
        optional :per_page, type: Integer, default: 20, allow_blank: false, values: ->(v) { v > 0 }
      end
    end
  end
end
