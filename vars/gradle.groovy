/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
                    stage('check stages') {
                        env.STAGE_NAME2 = 'check stages'
                        //String[] stages
                        stages = ['build & test','sonar','run','rest','nexus']
                        stagesToCheck = params.stage.split(';')
                        
                        if (params.stage != "")
                        {
                            for (i in stagesToCheck) {
                                if (params.stage.contains(env.STAGE_NAME2)) {
                                    env.FAIL_MESSAGE = "No existe el stage ${i}"
                                    error("No existe el stage ${i}")
                                }
                            }
                        }
                    }

                    stage('build & test') {
                        env.STAGE_NAME2 = 'build & test'

                        try {
                            stagesToCheck = params.stage.split(';')
                            if (params.stage.contains(env.STAGE_NAME2)) {
                                sh './gradlew clean build'
                            }
                        }
                        catch (e) {
                            env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                            error("Error")
                        }
                        
                    }
                    stage ('sonar') {
                        env.STAGE_NAME2 = 'sonar'
                        try {
                            if (params.stage.contains(env.STAGE_NAME2)) {
                                def scannerHome = tool 'sonar';
                                withSonarQubeEnv('Sonar') {
                                    sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                                }
                            }
                        }
                        catch (e) {
                            env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                            error("Error")
                        }
                    }
                    stage ('run') {
                        env.STAGE_NAME2 = 'run'
                        try {
                            if (params.stage.contains(env.STAGE_NAME2)) {
                                sh './gradlew bootRun &'
                                sleep 20
                            }
                        }
                        catch (e) {
                            env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                            error("Error")
                        }
                    }
                    stage ('rest') {
                        env.STAGE_NAME2 = 'rest'
                        try {
                            if (params.stage.contains(env.STAGE_NAME2)) {
                                sh 'curl -X GET http://localhost:8082/rest/mscovid/test?msg=testing'
                            }
                        }
                        catch (e) {
                            env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                            error("Error")
                        }
                    }
                    stage ('nexus') {
                        env.STAGE_NAME2 = 'nexus'
                        try {
                            if (params.stage.contains(env.STAGE_NAME2)) {
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
                        }
                        catch (e) {
                            env.FAIL_MESSAGE = "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]"
                            error("Error")
                        }
                    }
}

return this;