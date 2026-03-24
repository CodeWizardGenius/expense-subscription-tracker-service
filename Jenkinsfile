pipeline {
    agent any

    tools {
        jdk 'jdk17'
    }

    environment {
        MAVEN_OPTS = '-Xms256m -Xmx512m'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 15, unit: 'MINUTES')
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B -DskipTests clean package'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw -B test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Docker Build') {
            when {
                branch 'main'
            }
            steps {
                sh "docker build -t expense-tracker:${env.BUILD_NUMBER} ."
            }
        }
    }

    post {
        success {
            echo 'Build completed successfully.'
        }
        failure {
            echo 'Build failed.'
        }
        cleanup {
            cleanWs()
        }
    }
}
