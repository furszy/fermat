apply plugin: 'java'
apply plugin: 'jacoco'
apply plugin: 'findbugs'
apply plugin: 'checkstyle'
apply plugin: 'pmd'

sourceCompatibility = 1.7
version = '1.0'

repositories {
    mavenCentral()
}

configurations {
    cucumberRuntime {
        extendsFrom testRuntime
    }
}

dependencies {
    compile project(':fermat-api')
    compile project(':fermat-cbp-api')
    compile project(':fermat-pip-api')

    testCompile group: 'junit', name: 'junit', version: '4.11'
    testCompile group: 'org.easytesting', name: 'fest-assert-core', version: '2.0M10'
    testCompile group: 'com.googlecode.catch-exception', name: 'catch-exception', version: '1.2.0'
    testCompile 'info.cukes:cucumber-java:1.2.4'
    testCompile 'info.cukes:cucumber-junit:1.2.4'
}

pmd {
    toolVersion = '5.1.3'
}

tasks.withType (FindBugs) {
    reports {
        xml.enabled = false
        html.enabled = true
    }
}

def jacocoHtmlReport = ""

jacocoTestReport{
    reports{
        jacocoHtmlReport = "Code Coverage HTML Report: file://" + html.destination + "/index.html"
    }
}

task cucumber() {
    dependsOn assemble, compileTestJava
    doLast {
        javaexec {
            main = "cucumber.api.cli.Main"
            classpath = configurations.cucumberRuntime + sourceSets.main.output + sourceSets.test.output
            args = ['--plugin', 'pretty', '--glue', 'gradle.cucumber', 'src/test/resources']
        }
    }
}

task testCoverage(dependsOn: jacocoTestReport) << {
    println jacocoHtmlReport
}

jacocoTestReport.dependsOn clean, test
jacocoTestReport.mustRunAfter test
test.mustRunAfter clean
