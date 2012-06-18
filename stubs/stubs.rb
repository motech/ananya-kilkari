require 'rubygems'
require 'sinatra'

set :port, 1111

class RequestMapping
  attr_reader :status_code, :response_file, :content_type, :method, :path
  def initialize(method, path, status_code, response_file, content_type)
    @method = method
    @path = path
    @status_code = status_code
    @response_file = response_file
    @content_type = content_type
  end
end

class UserRequest
  attr_reader :response_status_code, :url, :body, :method
  def initialize(method, url, body, response_status_code)
    @method = method
    @url = url
    @body = body.read
    @response_status_code = response_status_code
  end

  def to_s
    "#{method} #{url} #{response_status_code}\n#{body}\n\n"
  end
end

class ProfileLoader
  @@user_requests = []

  def self.load(profile_file, user_requests = [])
    @@user_requests = user_requests
    request_mappings = []
    profile_file_path = profile_file

    content = File.readlines(profile_file_path).find_all{|line| !line.strip.empty? && !line.start_with?("#")}
    content.each do |line|
      request_path, response_values = line.split("=>").collect(&:strip)
      method, path = request_path.split("|").collect(&:strip)
      response_file, content_type, status_code = response_values.split("|").collect(&:strip)
      request_mappings << RequestMapping.new(method, path, status_code, response_file, content_type)
    end
    configure_requests(request_mappings)
  end

  def self.configure_requests(request_mappings)
    request_mappings.each do |request_mapping|
      block = Proc.new {
        content_type request_mapping.content_type
        status request_mapping.status_code
        @@user_requests << UserRequest.new(request.request_method, request.url, request.body, request_mapping.status_code)
        erb request_mapping.response_file.to_sym, params
      }

      send request_mapping.method, request_mapping.path, &block
    end
  end

  def self.user_requests
    @@user_requests
  end
end

if(ARGV[0] == nil || ARGV[0].empty?)
  puts "Profile File Path not provided.\n Usage: ruby stubs.rb kilkari.profile"
  exit
end
profile_file_path = ARGV[0]
ProfileLoader.load(profile_file_path)

get "/requests/:count" do
  user_requests = ProfileLoader.user_requests
  requests_count = params[:count].to_i

  start_index =  requests_count > user_requests.count ? 0 : ((user_requests.count - requests_count))
  end_index = user_requests.count
  range = start_index..end_index

  erb :requests, :locals => {:user_requests => user_requests[range].reverse}
end


get "/" do
  erb :home, :locals => {:current_profile => profile_file_path}
end


