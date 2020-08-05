namespace :grape do
  desc "Prints grape routes"
  task routes: :environment do
  	Api.routes.each do |api|
			method = api.request_method.ljust(10)
			path = api.path
			params = "     Params: [#{api.options[:params].keys.join(", ")}]"
			puts "#{method} #{path} #{params}"
		end
  end

end
