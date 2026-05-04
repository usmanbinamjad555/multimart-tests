pipeline {
    agent {
        docker {
            image 'markhobson/maven-chrome:jdk-17'
            args '-u root -v /var/run/docker.sock:/var/run/docker.sock --shm-size=2g'
        }
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Run Tests') {
            steps {
                echo 'Running Selenium Tests inside Maven-Chrome Container...'
                // Using 'mvn test' to run TestNG suite
                sh 'mvn clean test'
            }
        }
    }
    
    post {
        always {
            script {
                // Extracts the email of the person who pushed the current commit
                def COMMITTER_EMAIL = sh(script: "git --no-pager show -s --format='%ae' HEAD", returnStdout: true).trim()
                
                echo "Sending test results to the committer: ${COMMITTER_EMAIL}"
                
                emailext(
                    to: "${COMMITTER_EMAIL}",
                    subject: "Jenkins Pipeline Results: ${currentBuild.fullDisplayName}",
                    body: """
                        <h2>Build Status: ${currentBuild.currentResult}</h2>
                        <p>Project: MultiMart E-Commerce</p>
                        <p>Check the attached logs or view the build here: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    """,
                    attachLog: true,
                    mimeType: 'text/html'
                )
            }
        }
    }
}