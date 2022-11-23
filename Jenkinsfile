def uploadJarToNexus(artifactPath, pom) {
    nexusArtifactUploader(
        nexusVersion: NEXUS_VERSION,
        protocol: NEXUS_PROTOCOL,
        nexusUrl: NEXUS_URL,
        groupId: "${pom.groupId}",
        version: "${pom.version}",
        repository: NEXUS_REPOSITORY,
        credentialsId: NEXUS_CREDENTIAL_ID,
        artifacts: [
            // Artifact generated such as .jar, .ear and .war files.
            [artifactId: pom.artifactId,
            classifier: '',
            file: artifactPath,
            type: pom.packaging]
        ]
    )
}

pipeline{
    agent any

    environment {
        NEXUS_VERSION = "nexus3"
        // This can be http or https
        NEXUS_PROTOCOL = "http"
        // Where your Nexus is running. 'nexus-3' is defined in the docker-compose file
        NEXUS_URL = "146.190.104.63:8081"
        // Repository where we will upload the artifact
        NEXUS_REPOSITORY = "common-service"
        // Jenkins credential id to authenticate to Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexus-user-credentials"
    }
    stages{
        stage('Prepare workspace') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                echo 'Prepare workspace'
                // Clean workspace
                step([$class: 'WsCleanup'])
                // Checkout git
                checkout scm
            }
        }

        stage('Dependencies'){
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                echo 'Dependency stage'
                script {
                    sh "echo 'Downloading dependencies...'"
                    sh "mvn -s settings.xml clean install -DskipTests=true"
                }
            }
        }

        stage('Testing') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                echo 'Test stage'
                script {
                    sh "echo 'JUnit testing...'"
                    sh "mvn test -s settings.xml"
//                     sh "echo 'Integration testing...'"
//                     sh "mvn test -Dtest=IntegrationTest"
                    jacoco(execPattern: 'target/jacoco.exec')
                }
            }
        }

        stage('SonarQube Analysis') {
            agent {
                docker {
                    image 'jenkins/jnlp-agent-maven:jdk11'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            environment {
                scannerHome = tool 'SonarQube'
            }
            steps {
                withSonarQubeEnv(installationName: 'SonarQube') {
                    sh """mvn -s settings.xml clean verify sonar:sonar -Dsonar.projectKey=common-service \
                    -Dsonar.host.url=http://146.190.105.184:10000 -Dsonar.login=sqa_13efc056525ae8add04170822913d63831329f84
                    """
                }

                timeout(time: 60, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }

                script {
                    def sonar = waitForQualityGate()
                    if (sonar.status != 'OK') {
                        if (sonar.status == 'WARN') {
                            currentBuild.result = 'UNSTABLE'
                        } else {
                            error "Quality gate is broken"
                        }
                    }
                }
            }
        }

        stage("Deliver for development"){
            when {
//                 allOf {
//                     branch "develop"
//                     branch "origin/develop"
//                 }
                expression {
                    GIT_BRANCH = 'origin/' + sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    return GIT_BRANCH == 'origin/develop'
                }
            }
            steps{
                echo "========Push artifact to nexus========"
                script {
                    // Read POM xml file
                    pom = readMavenPom file: "pom.xml";
                    // Find built artifact under target folder
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");

                    // Extract the path from the File found
                    artifactPath = filesByGlob[0].path;

                    // Assign to a boolean response verifying If the artifact name exists
                    artifactExists = fileExists artifactPath;

                    if (artifactExists) {
                        uploadJarToNexus(artifactPath, pom)
                        echo "Published to nexus successfully"
                    } else {
                        error "-> File: ${artifactPath}, could not be found";
                    }
                }
            }
        }
    }
    post{
        always{
            echo "========always========"
        }
        aborted {
            echo "Sending message to Slack"
        }
        success{
            echo "========pipeline executed successfully ========"
        }
        failure{
            echo "========pipeline execution failed========"
        }
    }
}