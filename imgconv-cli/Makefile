ARTIFACT=imgconv-cli-0.0.0-SNAPSHOT

.PHONY: package
package:
	@mvn clean package 
	@mvn dependency:copy-dependencies -DincludeScope=runtime
	@rm -rf target/imgconv-cli
	@mkdir -p target/imgconv-cli/bin
	@mkdir -p target/imgconv-cli/lib
	@cp target/$(ARTIFACT).jar target/imgconv-cli/lib/.
	@cp target/dependency/* target/imgconv-cli/lib/.
	@cp skel/bin/convert target/imgconv-cli/bin/.
	@cd target && tar cvfz $(ARTIFACT).tar.gz imgconv-cli

.PHONY: clean
clean:
	@mvn clean

.PHONY: compile
compile:
	@mvn compile

.PHONY: test
test:
	@mvn test
