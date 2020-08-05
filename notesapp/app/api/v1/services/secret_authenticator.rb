module V1
  module Services
    module SecretAuthenticator
      def self.valid?(headers)
        headers[secret_header] == Rails.application.credentials.config[:api_v1_authentication_key]
      end

      def self.secret_header
        'Notesapp-V1-Secret'.freeze
      end
    end
  end
end
