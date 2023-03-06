ECR_URI                         = "NUMB.dkr.ecr.us-east-1.amazonaws.com"
DEPLOYER_ACC                    = "deployer-acc"
DEPLOY_ENV                      = params.DEPLOY_ENV
GITHUB_CREDENTIALS              = "example-github-credentials"
GITHUB_INFRA_REPO               = "https://github.com/projectx/infra.git"
INFRA_DIR                       = "project-infra"
NAMESPACE                       = params.BLUE_GREEN
INFRA_BRANCH                    = "master"
HELM_DIR                        = "helm-single"

def image_version               = params.image_version

properties([
    parameters([

        choice(
            name: 'DEPLOY_ENV',
            choices: 'dev\nqa\nprod'
        ),

        choice(
            name: 'BLUE_GREEN',
            choices: 'blue\ngreen'
        ),

        [$class: 'ChoiceParameter', 
        choiceType: 'PT_SINGLE_SELECT', 
        description: '', 
        filterable: false, 
        name: 'image_version', 
        randomName: 'choice-parameter-21337077649621571', 
            script: [
                $class: 'GroovyScript', 
                fallbackScript: [
                    classpath: [], 
                    sandbox: false, 
                    script: 'return ["ERROR"]'], 
            script: [
                classpath: [], 
                sandbox: false, 
                script: '''
import groovy.json.JsonSlurper
def ecr_images_json = ['bash', '-c', "aws ecr list-images --repository-name YOUR_REPOSITORY_NAME --filter tagStatus=TAGGED --region REGION"].execute().text
def data = new JsonSlurper().parseText(ecr_images_json)
def ecr_images = []; 
data.imageIds.each {
    if ( ( "$it.imageTag".length() >= 1 ) && ( "$it.imageTag" != "latest" ) && ( !"$it.imageTag".contains("-pr-") ) ) {
        ecr_images.push("$it.imageTag")
        }
}
ecr_images.sort{ a, b -> 
    def aList = a.findAll(/\\d+/)
    def bList = b.findAll(/\\d+/)
    for ( int i = 0 ; i < aList.size() ; i++ ) {
        def aVal = aList[i] ? aList[i].toInteger() : 0
        def bVal = bList[i] ? bList[i].toInteger() : 0
        if ( aVal <=> bVal ) { return aVal <=> bVal }    }
    bList.size() > aList.size() ? -1 : 0 }
ecr_images.push("NA")
return ecr_images.reverse()
                '''
            ]
            ]
        ],

        [$class: 'ChoiceParameter', 
        choiceType: 'PT_SINGLE_SELECT', 
        description: '', 
        filterable: false, 
        name: 'dots_analytics_intake_image_version', 
        randomName: 'choice-parameter-21337077649521571', 
            script: [
                $class: 'GroovyScript', 
                fallbackScript: [
                    classpath: [], 
                    sandbox: false, 
                    script: 'return ["ERROR"]'], 
            script: [
                classpath: [], 
                sandbox: false, 
                script: '''
import groovy.json.JsonSlurper
def ecr_images_json = ['bash', '-c', "aws ecr list-images --repository-name dots-microservices --filter tagStatus=TAGGED --region us-east-1"].execute().text
def data = new JsonSlurper().parseText(ecr_images_json)
def ecr_images = []; 
data.imageIds.each {
    if ( ( "$it.imageTag".length() >= 1 ) && ( "$it.imageTag" != "latest" ) && ( !"$it.imageTag".contains("-pr-") ) ) {
        ecr_images.push("$it.imageTag")
        }
}
ecr_images.sort{ a, b -> 
    def aList = a.findAll(/\\d+/)
    def bList = b.findAll(/\\d+/)
    for ( int i = 0 ; i < aList.size() ; i++ ) {
        def aVal = aList[i] ? aList[i].toInteger() : 0
        def bVal = bList[i] ? bList[i].toInteger() : 0
        if ( aVal <=> bVal ) { return aVal <=> bVal }    }
    bList.size() > aList.size() ? -1 : 0 }
ecr_images.push("NA")
return ecr_images.reverse()
                '''
            ]
            ]
        ]) // parameters
]) // properties
