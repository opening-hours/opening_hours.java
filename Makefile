VALUE = "Mo-Fr 08:00-16:00"

default: run

installDependencies: node_modules/opening_hours

node_modules/opening_hours:
	npm install opening_hours

run: OpeningHourTest.class installDependencies
	java $(<:.class=) $(VALUE)
	java $(<:.class=) "error"

%.class: %.java
	javac "$<"

clean:
	rm -rf *.class
