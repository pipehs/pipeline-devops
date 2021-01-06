/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/
import pipeline.*

def call(){
    figlet 'gradle'
    def utils = new test.UtilMethods()
    def pipelineStages = utils.getCiCdStages(JOB_NAME)

    if (!pipelineStages)
    {
        env.FAIL_MESSAGE = "Ejecución invalida; favor revise el Job que está ejecutando"
        error FAIL_MESSAGE
    }
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

//--Stages de Integración continua--//
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

def nexusCI() {
    nexusPublisher nexusInstanceId: 'nexus',
    nexusRepositoryId: 'test-nexus',
    packages: [[$class: 'MavenPackage',
        mavenAssetList: [[classifier: '',
            extension: 'jar',
            filePath: "build/DevOpsUsach2020-0.0.1-develop.jar"]],
            mavenCoordinate: [
                artifactId: 'DevOpsUsach2020',
                groupId: 'com.devopsusach2020',
                packaging: 'jar',
                version: "0.0.1-develop"
            ]
    ]]
}

//--Stages de entrega continua (CD)--//
def downloadNexus() {
    sh "curl -X GET -u admin:admin http://localhost:8081/repository/test-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1-develop/DevOpsUsach2020-0.0.1-develop.jar -O"
}

def runDownloadedJar() {
    figlet 'runDownloadedJar'
    sh "nohup java -jar DevOpsUsach2020-0.0.1-develop.jar &"
    sleep 20
}

def nexusCD() {
    figlet 'nexusCD'
    nexusPublisher nexusInstanceId: 'nexus',
    nexusRepositoryId: 'test-nexus',
    packages: [[$class: 'MavenPackage',
        mavenAssetList: [[classifier: '',
            extension: 'jar',
            filePath: "/root/.jenkins/workspace/ci-cd/pipeline-cd/build/libs/DevOpsUsach2020-release-v1-0-0.jar"]],
            mavenCoordinate: [
                artifactId: 'DevOpsUsach2020',
                groupId: 'com.devopsusach2020',
                packaging: 'jar',
                version: "release-v1-0-0"
            ]
    ]]
}
return this;