require 'rubygems'
require 'sinatra'

set :port, 1111

class RequestMapping
  attr_reader :status_code, :response_file, :content_type, :method, :path, :request_id
  def initialize(request_id, method, path, status_code, response_file, content_type)
    @request_id = request_id
    @method = method
    @path = path
    @status_code = status_code
    @response_file = response_file
    @content_type = content_type
  end
end

class RequestMappings
  def initialize(request_mappings)
    @request_mappings = request_mappings
  end

  def request_mapping_of(method, path)
    @request_mappings.find {|request_mapping| request_mapping.path.upcase == path.upcase && request_mapping.method.upcase == method.upcase}
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
  def self.load
    request_mappings = []
    if(ARGV[0] == nil || ARGV[0].empty?)
      puts "Profile File Path not provided.\n Usage: ruby stubs.rb kilkari.profile"
      exit
    end
    profile_file_path = ARGV[0]
    content = File.readlines(profile_file_path).find_all{|line| !line.strip.empty? && !line.start_with?("#")}
    content.each_with_index do |line, index|
      request_path, response_values = line.split("=>").collect(&:strip)
      method, path = request_path.split("|").collect(&:strip)
      response_file, content_type, status_code = response_values.split("|").collect(&:strip)
      request_mappings << RequestMapping.new(index, method, path, status_code, response_file, content_type)
    end
    request_mappings
  end
end

request_mappings = ProfileLoader.load
reader = RequestMappings.new(request_mappings)
user_requests = []

request_mappings.each do |request_mapping|
  send request_mapping.method, request_mapping.path do
      params.delete("captures")
      params.delete("splat")
      request_path = request.path
      params.each do |key,value|
        request_path.sub!("/#{value}", "/:#{key}") if value != nil
      end

      request_mapping = reader.request_mapping_of(request.request_method, request_path)
      content_type request_mapping.content_type
      status request_mapping.status_code
      
      user_requests << UserRequest.new(request.request_method, request.url, request.body, request_mapping.status_code)
      erb request_mapping.response_file.to_sym, params
   end
end

get "/requests/:count" do
  requests_count = params[:count].to_i

  start_index =  requests_count > user_requests.count ? 0 : ((user_requests.count - requests_count))
  end_index = user_requests.count
  range = start_index..end_index

  user_requests[range].collect(&:to_s).reverse
end
