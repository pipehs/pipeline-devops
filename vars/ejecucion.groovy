def call() {
    pipeline {
        agent any
        environment { 
            USER_NAME = 'Felipe Herrera Seguel'
        }
        parameters {
            choice(name:'CHOICE', choices:['gradle','maven'], description: 'Elección de herramienta de construcción')
            string(name:'stage', defaultValue:'', description:'')
        }
        stages {
            stage('Pipeline') {
                steps {
                    script {
                        if (params.CHOICE == 'gradle')
                        {
                            gradle.call()
                        }
                        else
                        {
                            maven.call()
                        }
                    }
                }
            }
        }
        post {
            
            success {
                slackSend channel: 'U01E2R4SXRN', 
                color: 'good', 
                message: "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}] Ejecución exitosa", 
                teamDomain: 'dipdevopsusach2020', 
                tokenCredentialId: 'slack-token'
            }
            failure {
                slackSend channel: 'U01E2R4SXRN', 
                color: 'danger', 
                message: "${FAIL_MESSAGE}", 
                teamDomain: 'dipdevopsusach2020', 
                tokenCredentialId: 'slack-token'
            }
        }
    }
}

return this;
