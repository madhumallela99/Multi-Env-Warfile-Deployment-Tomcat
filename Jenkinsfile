pipeline {
    agent any

    parameters {
        string(
            name: 'BRANCH_NAME',
            defaultValue: 'dev',
            description: 'Enter branch name: dev | qa | prod'
        )
    }

    environment {
        WAR_NAME = "simple-app.war"
        TOMCAT_PATH = "/opt/tomcat/webapps"

        DEV_SERVER  = "ec2-user@DEV_IP"
        QA_SERVER   = "ec2-user@QA_IP"
        PROD_SERVER = "ec2-user@PROD_IP"
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo "Checking out branch: ${params.BRANCH_NAME}"
                git branch: "${params.BRANCH_NAME}",
                    url: "https://github.com/your-repo/simple-war-app.git"
            }
        }

        stage('Build WAR') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Deploy Based on Branch') {
            steps {
                script {

                    if (params.BRANCH_NAME == 'dev') {

                        echo "Deploying to DEV"
                        sh """
                        scp target/${WAR_NAME} ${DEV_SERVER}:${TOMCAT_PATH}/
                        """

                    } else if (params.BRANCH_NAME == 'qa') {

                        echo "Deploying to QA"
                        sh """
                        scp target/${WAR_NAME} ${QA_SERVER}:${TOMCAT_PATH}/
                        """

                    } else if (params.BRANCH_NAME == 'prod') {

                        echo "Waiting for PROD approval"
                        input message: 'Approve deployment to PROD?'

                        echo "Taking PROD backup"
                        sh """
                        ssh ${PROD_SERVER} '
                        if [ -f ${TOMCAT_PATH}/${WAR_NAME} ]; then
                            cp ${TOMCAT_PATH}/${WAR_NAME} ${TOMCAT_PATH}/${WAR_NAME}.backup
                        fi
                        '
                        """

                        echo "Deploying to PROD"
                        sh """
                        scp target/${WAR_NAME} ${PROD_SERVER}:${TOMCAT_PATH}/
                        """

                    } else {
                        error "Invalid branch name. Use dev | qa | prod"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Deployment successful for ${params.BRANCH_NAME}"
        }

        failure {
            script {
                if (params.BRANCH_NAME == 'prod') {
                    echo "Deployment failed! Rolling back PROD..."

                    sh """
                    ssh ${PROD_SERVER} '
                    if [ -f ${TOMCAT_PATH}/${WAR_NAME}.backup ]; then
                        mv ${TOMCAT_PATH}/${WAR_NAME}.backup ${TOMCAT_PATH}/${WAR_NAME}
                    fi
                    '
                    """
                }
            }
        }
    }
}
