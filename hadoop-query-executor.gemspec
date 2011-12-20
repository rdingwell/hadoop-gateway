## hadoop-query-executor.gemspec
#

Gem::Specification::new do |spec|
  spec.name = "hadoop-query-executor"
  spec.version = "0.1"
  spec.platform = 'java'
  spec.summary = "hadoop-query-executor"
  spec.description = "description: it's java"

  spec.files = Dir['{lib,src}/**/*'] + Dir['{*.gemspec,Rakefile}']

  spec.executables = []
  
  spec.require_path = "lib"

  spec.test_files = nil

  spec.extensions.push(*[])

  spec.author = "Rob Dingwell"
  spec.email = "bobd@mitre.org"
  spec.homepage = "https://github.com/hquery/hadoop-query-executor"
end
