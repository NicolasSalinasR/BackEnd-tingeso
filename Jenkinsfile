pipeline {
    agent any
    tools{
        maven 'maven_3_8_1'
    }
    stages{
        stage('Build maven'){
            steps{
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/NicolasSalinasR/BackEnd-tingeso']])
                bat 'mvn clean package'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run Maven 'test' phase. It compiles the test sources and runs the unit tests
                bat 'mvn test' // Use 'bat' for Windows agents or 'sh' for Unix/Linux agents
            }
        }

        stage('Build docker image'){
            steps{
                script{
                    bat 'docker build -t haruhisomu/backend:latest .'
                }
            }
        }

        stage('Push image to Docker Hub'){
            steps{
                script{
                   withCredentials([string(credentialsId: 'dockerPass', variable: 'DOCKER_HUB_PASSWORD')]) {
                        bat 'docker login -u haruhisomu -p %DOCKER_HUB_PASSWORD%'
                   }
                   bat 'docker push haruhisomu/backend:latest'
                }
            }
        }
    }
}