require 'jars/hadoop-gateway.jar'
require 'jars/hadoop-core-0.20.205.0.jar'
require 'jars/commons-logging-1.1.1.jar'
require 'jars/commons-configuration-1.6.jar'
require 'jars/core-3.1.1.jar'
require 'jars/commons-lang-2.4.jar'
require 'query_utilities'
require 'json'
class HadoopQueryExecutor 

    @executor = nil
    
    def initialize(map,reduce,functions,query_id,filter)
        @executor = org.projecthquery.hadoop.HadoopQueryExecutor.new(map, reduce, functions + QueryUtilities.patient_api_javascript, filter, query_id)
        @executor.setJar(File.join(File.dirname(__FILE__),'jars','hadoop-gateway.jar'))
    end
   
    
   def execute
    result= @executor.execute()
    
    results = {}
    result.key_set.each do |k|
     key = parse_if_needed(k)
     val = parse_if_needed(result[k])
     key = QueryUtilities.stringify_key(k)
     results[key] = val
    end
        return results
    end


   private 
   
   def parse_if_needed(x)
     (x[0] == '{') ? JSON.parse(x) : x
   end
end