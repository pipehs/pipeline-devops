/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

                    stage('build & test') {
                        env.STAGE_NAME2 = 'build & test'
                        sh './gradlewsss clean build'
                    }
                    stage ('sonar') {
                        
                        def scannerHome = tool 'sonar';
                        withSonarQubeEnv('Sonar') {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                        }
                    }
                    stage ('run') {
                        
                        sh './gradlewsss bootRun &'
                        sleep 20
                    }
                    stage ('rest') {
                        environment { 
                            STAGE_NAME2 = 'rest'
                        }
                        sh 'curl -X GET http://localhost:8082/rest/mscovid/test?msg=testing'
                    }
                    stage ('nexus') {
                        environment { 
                            STAGE_NAME2 = 'nexus'
                        }
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