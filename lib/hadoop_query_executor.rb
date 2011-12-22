require 'lib/hadoop-gateway.jar'
require 'ivy_lib/hadoop-core-0.20.205.0.jar'
require 'ivy_lib/commons-logging-1.1.1.jar'
require 'ivy_lib/commons-configuration-1.6.jar'
require 'ivy_lib/core-3.1.1.jar'
require 'ivy_lib/commons-lang-2.4.jar'
#require 'query_utilities'
class HadoopQueryExecutor 

    @executor = nil
    
    def initialize(map,reduce,functions,query_id,filter)
        @executor = org.projecthquery.hadoop.HadoopQueryExecutor.new(map, reduce, functions, filter, query_id)
        @executor.setJar(File.join(File.dirname(__FILE__),'lib','hadoop-gateway.jar'))
    end
   
    
   def execute
    result= @executor.execute()
    return result
    results = []
    result.key_set.each do |k|
     key = QueryUtilities.stringify_key(k)
     results[key] = result[k]
    end
        return results
    end

end