/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
        stage('check stages') {
                        env.STAGE_NAME2 = 'check stages'
                        //String[] stages
                        stages = ['Compile','Test Code','Jar','SonarQube analysis','uploadNexus']
                        stagesToCheck = params.stage.split(';')
                        
                        if (params.stage != "")
                        {
                            for (i in stagesToCheck) {
                                if (params.stage.contains(env.STAGE_NAME2) || params.stage == "") {
                                    env.FAIL_MESSAGE = "No existe el stage ${i}"
                                    error("No existe el stage ${i}")
                                }
                            }
                        }
        }

        stage('Compile') {
                env.STAGE_NAME2 = 'Compile'
                try {
                    sh './mvnw clean compile -e'
                }
                catch (e) {
                    env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                    error("Error")
                }
            }
            
            stage('Test Code') {
                env.STAGE_NAME2 = 'Test Code'
                try {
                    sh './mvnw clean test -e'
                }
                catch (e) {
                    env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                    error("Error")
                }
            }
            stage('Jar') {
                env.STAGE_NAME2 = 'Jar'
                try {
                    sh './mvnw clean package -e'
                }
                catch (e) {
                    env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                    error("Error")
                }
            }
            stage('SonarQube analysis') {
                env.STAGE_NAME2 = 'SonarQube analysis'
                try {
                        def scannerHome = tool 'sonar';
                        withSonarQubeEnv('Sonar') {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                        }
                }
                catch (e) {
                    env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                    error("Error")
                }
            }
            stage('uploadNexus') {
                env.STAGE_NAME2 = 'uploadNexus'
                try {
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
                            ]
                        ]
                }
                catch (e) {
                    env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                    error("Error")
                }
            }
}

return this;