def call() {
    pipeline {
        agent any
        environment { 
            USER_NAME = 'Felipe Herrera Seguel'
        }
        parameters {choice(name:'CHOICE', choices:['gradle','maven'], description: 'Elección de herramienta de construcción')}
        stages {
            stage('Pipeline') {
                steps {
                    script {
                        if (params.CHOICE == 'gradle')
                        {
                            def ejecucion = load 'gradle.groovy'
                            ejecucion.call()
                        }
                        else
                        {
                            def ejecucion = load 'maven.groovy'
                            ejecucion.call()
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
                message: "[${USER_NAME}] [${JOB_NAME}] [${params.CHOICE}]  Ejecución fallida en [${STAGE_NAME2}]", 
                teamDomain: 'dipdevopsusach2020', 
                tokenCredentialId: 'slack-token'
            }
        }
    }
}

return this;
