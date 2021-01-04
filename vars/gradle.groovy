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

                        for (i in stagesToCheck) {
                            if (!stages.containsAll(i)) {
                                env.FAILURE_MESSAGE2 = 1
                                error("No existe el stage ${i}")
                            }
                        }
                    }

                    stage('build & test') {
                        env.STAGE_NAME2 = 'build & test'
                        sh './gradlew clean build'
                    }
                    stage ('sonar') {
                        env.STAGE_NAME2 = 'sonar'
                        def scannerHome = tool 'sonar';
                        withSonarQubeEnv('Sonar') {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                        }
                    }
                    stage ('run') {
                        env.STAGE_NAME2 = 'run'
                        sh './gradlew bootRun &'
                        sleep 20
                    }
                    stage ('rest') {
                        env.STAGE_NAME2 = 'rest'
                        sh 'curl -X GET http://localhost:8082/rest/mscovid/test?msg=testing'
                    }
                    stage ('nexus') {
                        env.STAGE_NAME2 = 'nexus'
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

return this;