#!groovy

// If you would like to use any shared Library it must be included at the top of the file
@Library('rpmlib') _

pipeline {
    agent any
    parameters {
        // Define any parameters which you would like to ask from the user here
        string(name: 'param1', defaultValue: 'value1', description: 'Description')
    }
    environment {
        var1: value1
    }
    stages{
        stage('checkout'){
            steps {
                checkout scm
            }
        }
        stage('Package'){
           agent {
                // Instruct Jenkins to run this stage inside a docker container
                docker {
                    // Image name. Have a look at https://git.stepping-stone.ch/stoney-storage/Jenkins-docker for a list of images
                    image 'sst/build-base:latest'
                    // any additional commandline arguments to the docker run command see man docker-run for details
                    // -e jenkins=true is required by the mkRPMBuild-simple.sh to run the correct commands for jenkins
                    // -v ${env.JENKINS_HOME}/.m2:/home/rpmbuild/.m2 is used to cache any downloaded maven jars on the Jenkins node and thus speed up subsequent builds. 
                    // This trick may be adapted for other commands aswell
                    args  "-v ${env.JENKINS_HOME}/.m2:/home/rpmbuild/.m2"
                    // Point Jenkins to the localy installed registry instead of docker Hub. As we don't have any images on docker Hub this is a required Line
                    registryUrl 'http://localhost:5000'
                }
            }
            steps {
                // Use any command as if it is installed on the Jenkins Server. It will be transparently executed inside the Docker container named
                // in the agent section above
                // The command /usr/bin/mkRPMBuild-simple.sh is a script made by sst-twe to simplify invoking of the rpmbuild commands as documented in this Wiki
                sh '/usr/bin/bash -ex /usr/local/scripts/build.sh'
            }
            post {
                always {
                    // Always remember to stash generated files for later stages
                    stash includes: '*.rpm', name: 'RPMs'
                }
                cleanup {
                    // Cleanup gets always executes last in a stage.
                    // If you build inside a docker image you might get Permission problems when the next stage is run on the Jenkins node directly again or when 
                    // a new Build uses that workspace. Thus I always recomend to use the cleanWS() function while still inside the docker image to cleanse anything nasty
                    cleanWs()
                }
            }
        }
    }
}
