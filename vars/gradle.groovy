/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/
import pipeline.*

def call(){
    figlet 'gradle'
    def pipelineStages = ['buildAndtest','sonar','runJar','rest','nexus']
    def utils = new test.UtilMethods()
    def stages = utils.getValidatedStages(params.stage, pipelineStages)
    env.FAIL_MESSAGE = ""
    stages.each{
        stage(it){
            try {
                "${it}"()
            }
            catch (e) {
                env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${it}]"
                error "Stage ${it} tiene problemas: ${e}"
            }
        }
    }
}

def buildAndTest() {
    sh './gradlew clean build'
}

def sonar() {
    def scannerHome = tool 'sonar';
    withSonarQubeEnv('Sonar') {
        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
    }
}

def runJar() {
     sh './gradlew bootRun &'
    sleep 20
}

def rest() {
    sh 'curl -X GET http://localhost:8082/rest/mscovid/test?msg=testing'
}

def nexus() {
    nexusPublisher nexusInstanceId: 'nexus',
    nexusRepositoryId: 'test-nexus',
    packages: [[$class: 'MavenPackage',
        mavenAssetList: [[classifier: '',
            extension: 'jar',
            filePath: '/root/.jenkins/workspace/ltibranch-pipeline_feature-nexus/build/DevOpsUsach2020-0.0.1.jar']],
            mavenCoordinate: [
                artifactId: 'DevOpsUsach2020',
                groupId: 'com.devopsusach2020',
                packaging: 'jar',
                version: '0.0.1'
            ]
    ]]
}

return this;