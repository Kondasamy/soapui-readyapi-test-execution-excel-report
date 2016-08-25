package com.kondasamy.soapui.plugin

/**
 * Created by Kondasamy J
 * Contact: Kondasamy@outlook.com
 */

import com.eviware.soapui.SoapUI
import com.eviware.soapui.model.project.Project
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.plugins.ActionConfiguration
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.support.action.support.AbstractSoapUIAction

@ActionConfiguration(actionGroup = ActionGroups.OPEN_PROJECT_ACTIONS)
class ProjectExportTestReport extends AbstractSoapUIAction <Project>
{
    public ProjectExportTestReport()
    {
        super("Plugin:Export Test Report", "Exports test execution report to a CSV file")
    }

    @Override
    void perform(Project project, Object o)
    {
        /*
         * Create CSV file in the User directory
         */
        def currentUser = System.getProperty('user.name')
        def today= new Date().format("yyyyMMdd")
        def fileName = today+"_SoapUI_Test_Results.csv"
        def mainDir = System.getProperty('user.home')
        def SubDir = "\\SoapUI Data\\"
        def SubDir1 = new File(mainDir,SubDir)
        def file
        if(SubDir1.exists())
        {
            file = new File(SubDir1,fileName)
        }
        else
        {
            SubDir1.mkdirs()
            file = new File(SubDir1,fileName)
        }
        //Append header in CSV file
        if(file.exists())
        {
            file.append("PROJECT,TYPE,TEST CASE NAME,TEST STEP COUNT,TEST STATUS,EXECUTED BY\n")
        }
        /*
         * Iterate through each test cases and collect the data
         */
        def projName = project.name
        project.getTestSuiteList().each
                {
                    testSuite->
                        def tsName = testSuite.name
                        testSuite.getTestCaseList().each
                                {
                                    testCase->
                                        def tcName = testCase.name
                                        def testStepCount = testCase.testStepCount
                                        def testRunFlag = 0
                                        testCase.getTestStepsOfType(com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep.class).each
                                                {
                                                    tests->
                                                        def response = tests.httpRequest.response.getRawResponseData()
                                                        if( response == null || response.length == 0 )
                                                        {
                                                            SoapUI.log.warn "Empty Response data for TestStep : "+ tests.testStep.testCase.testSuite.name + "=>"+tests.testStep.testCase.name+ "->"+tests.name
                                                        }
                                                        else
                                                        {
                                                            def tstName = tests.getName()
                                                            SoapUI.log.info "Assertion Status: "+tests.assertionStatus +"Test Step : " +tstName
                                                        }
                                                }
                                        testCase.getTestStepsOfType(com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep.class).each
                                                {
                                                    tests->
                                                        def response = tests.httpRequest.response.getRawResponseData()
                                                        if( response == null || response.length == 0 )
                                                        {
                                                            SoapUI.log.warn "Empty Response data for TestStep : "+ tests.testStep.testCase.testSuite.name + "=>"+tests.testStep.testCase.name+ "->"+tests.name
                                                        }
                                                        else
                                                        {
                                                            def tstName = tests.getName()
                                                            SoapUI.log.info "Assertion Status: "+tests.assertionStatus +"Test Step : " +tstName
                                                        }

                                                }
                                        SoapUI.log.info "Test Case Name : "+ tcName
                                        file.append("$projName,$tsName,$tcName,$testStepCount,PASS,$currentUser")
                                }

                }
        UISupport.showInfoMessage("Artifacts Successfully exported!! Please see the SoapUI log for more information!","File Export Success!!!")
    }
}
/**
 * Created by Kondasamy J
 * Contact: Kondasamy@outlook.com
 */