pipeline {
    agent none
    stages {
        stage('Build and Unit Test') {
            agent {
                dockerfile {
                    filename 'Dockerfile'
                    dir 'scripts/ci/docker'
                    label 'docker'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                sh 'scripts/ci/test-and-package-all.sh'
                junit '*/build/test-results/test/*.xml'
            }
        }
        stage('Test and Analyze') {
            parallel {
                stage('Analyze') {
                    agent {
                        dockerfile {
                            filename 'Dockerfile'
                            dir 'scripts/ci/docker'
                            label 'docker'
                            args '-v $HOME/.m2:/root/.m2'
                        }
                    }
                    steps {
                        sh 'scripts/ci/generate-findbugs-report.sh'
                        // TODO: Stash
                    }
                }
                stage('Docs') {
                    agent {
                        dockerfile {
                            filename 'Dockerfile'
                            dir 'scripts/ci/docker'
                            label 'docker'
                            args '-v $HOME/.m2:/root/.m2'
                        }
                    }
                    steps {
                        sh 'scripts/ci/generate-javadoc.sh'
                        // TODO: Stash
                    }
                }
                stage('Integration') {
                    agent {
                        dockerfile {
                            filename 'Dockerfile'
                            dir 'scripts/ci/docker'
                            label 'docker'
                            args '-v $HOME/.m2:/root/.m2'
                        }
                    }
                    steps {
                        withEnv([
                                'SPRING_VERSION=4.2.7.RELEASE',
                                'SPRING_TEST_COMPONENT=true'
                        ]) {
                            sh 'scripts/ci/test-integration.sh'
                            junit 'spring-test/build/test-results/test/*.xml'
                        }
                    }
                }
                stage('Integration - Spring') {
                    agent {
                        dockerfile {
                            filename 'Dockerfile'
                            dir 'scripts/ci/docker'
                            label 'docker'
                            args '-v $HOME/.m2:/root/.m2'
                        }
                    }
                    steps {
                        withEnv([
                                'SPRING_VERSION=4.2.7.RELEASE',
                                'SPRING_TEST_COMPONENT=true'
                        ]) {
                            sh 'scripts/ci/test-spring-compatibility.sh'
                            junit 'spring-test/build/test-results/test/*.xml'
                        }
                    }
                }
            }
        }
        stage('Publish Snapshot') {
            agent {
                dockerfile {
                    filename 'Dockerfile'
                    dir 'scripts/ci/docker'
                    label 'docker'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                withCredentials([
                        usernamePassword(
                                credentialsId   : 'mutr-sign-passphrase',
                                usernameVariable: 'SIGNING_KEY_ID',
                                passwordVariable: 'SIGNING_PASSWORD'
                        ),
                        usernamePassword(
                                credentialsId: 'mutr-external-repository',
                                usernameVariable: 'DEPLOY_USERNAME',
                                passwordVariable: 'DEPLOY_PASSWORD'
                        ),
                        file(credentialsId: 'mutr-sign-key', variable: 'SIGNING_KEY_RING_FILE'),
                ]) {
                    sh 'scripts/ci/release.sh'
                }
            }
        }
    }
}