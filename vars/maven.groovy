/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

        stage('Compile') {
                    sh './mvnw clean compile -e'
            }
            
            stage('Test Code') {
                    sh './mvnw clean test -e'
            }
            stage('Jar') {
                    sh './mvnw clean package -e'
            }
            stage('SonarQube analysis') {
                        def scannerHome = tool 'sonar';
                        withSonarQubeEnv('Sonar') {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                        }
            }
            stage('uploadNexus') {
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