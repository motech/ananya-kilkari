require 'rubygems'
require 'sinatra'

set :port, 1111

method = 'get'
path = "/testing/"
contenttype = ":json"

# get path do
#     content_type :json
#     "{ 'id' : '#{params[:a]}'}"
# end

# send :get, path do
#     content_type :json
#     "{ 'id' : '#{params[:a]}'}"
# end

for index in 1..5 do
	send :get, path + "#{index}" do
	    content_type :json 
	    "{ 'id' : '#{index}'}"
	end
end

