package com.kondasamy.soapui.plugin

/**
 * Created by Kondasamy J
 * Contact: Kondasamy@outlook.com
 */

import com.eviware.soapui.SoapUI
import com.eviware.soapui.model.project.Project
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
        if(file.length()==0)
            file.append("PROJECT,TYPE,TEST CASE NAME,TEST STEP COUNT,TEST STATUS,EXECUTED BY\n")

        /*
         * Iterate through each test cases and collect the data
         */
        def projName = project.name
        project.getTestSuiteList().each
                {
                    testSuite->
                        //Ignore the disabled test suites
                        if(!testSuite.isDisabled())
                        {
                            def tsName = testSuite.name
                            testSuite.getTestCaseList().each
                                    {
                                        testCase->
                                            if(!testCase.isDisabled())
                                            {
                                                def tcName = testCase.name
                                                def testStepCount = testCase.testStepCount
                                                def testCasePassStatusFlag =0
                                                def testCaseFailStatusFlag =0
                                                String testCaseStatus
                                                testCase.getTestStepsOfType(com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep.class).each
                                                        {
                                                            testRequestStep->
                                                                if(!testRequestStep.isDisabled() && testRequestStep.httpRequest.response!=null)
                                                                {
                                                                    String testStepStatus
                                                                    def response = testRequestStep.httpRequest.response.getRawResponseData()
                                                                    if( response == null || response.length == 0 )
                                                                    {
                                                                        SoapUI.log.warn "Empty Response data for TestStep : "+ testRequestStep.testStep.testCase.testSuite.name + "=>"+testRequestStep.testStep.testCase.name+ "->"+testRequestStep.name
                                                                    }
                                                                    else
                                                                    {
                                                                        def tstName = testRequestStep.getName()
                                                                        //Loop through each assertion and conclude the test step status
                                                                        def assertionPassStatusFlag = 0
                                                                        def assertionFailStatusFlag = 0
                                                                        testRequestStep.assertionList.each
                                                                                {
                                                                                    assertion->
                                                                                        if(assertion.status.toString().equalsIgnoreCase("VALID"))
                                                                                            assertionPassStatusFlag++
                                                                                        else if(assertion.status.toString().equalsIgnoreCase("FAILED"))
                                                                                            assertionFailStatusFlag++
                                                                                }
                                                                        if(assertionPassStatusFlag>0 && assertionFailStatusFlag==0)
                                                                            testStepStatus = "PASS"
                                                                        else if(assertionFailStatusFlag>0)
                                                                            testStepStatus = "FAIL"
                                                                        else
                                                                            testStepStatus = "NO RUN"
//                                                                        SoapUI.log.info "Test case status: " + testStepStatus + " Test case : " + tcName
                                                                    }

                                                                    //Loop through the test step status and conclude the test case status
                                                                    if(testStepStatus.equalsIgnoreCase("PASS"))
                                                                        testCasePassStatusFlag++
                                                                    else if (testStepStatus.equalsIgnoreCase("FAIL"))
                                                                        testCaseFailStatusFlag++
                                                                }
                                                        }
                                                testCase.getTestStepsOfType(com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep.class).each
                                                        {
                                                            testRequestStep ->
                                                                if (!testRequestStep.isDisabled() && testRequestStep.httpRequest.response != null)
                                                                {
                                                                    String testStepStatus
                                                                    def response = testRequestStep.httpRequest.response.getRawResponseData()
                                                                    if (response == null || response.length == 0)
                                                                    {
                                                                        //SoapUI.log.warn "Empty Response data for TestStep : " + testRequestStep.testStep.testCase.testSuite.name + "=>" + testRequestStep.testStep.testCase.name + "->" + testRequestStep.name
                                                                    }
                                                                    else
                                                                    {
                                                                        def tstName = testRequestStep.getName()
                                                                        //Loop through each assertion and conclude the test step status
                                                                        def assertionPassStatusFlag = 0
                                                                        def assertionFailStatusFlag = 0
                                                                        testRequestStep.assertionList.each
                                                                                {
                                                                                    assertion ->
                                                                                        if (assertion.status.toString().equalsIgnoreCase("VALID"))
                                                                                            assertionPassStatusFlag++
                                                                                        else if (assertion.status.toString().equalsIgnoreCase("FAILED"))
                                                                                            assertionFailStatusFlag++
                                                                                }
                                                                        if (assertionPassStatusFlag > 0 && assertionFailStatusFlag == 0)
                                                                            testStepStatus = "PASS"
                                                                        else if (assertionFailStatusFlag > 0)
                                                                            testStepStatus = "FAIL"
                                                                        else
                                                                            testStepStatus = "NO RUN"
//                                                                        SoapUI.log.info "Test case status: " + testStepStatus + " Test case : " + tcName
                                                                    }

                                                                    //Loop through the test step status and conclude the test case status
                                                                    if (testStepStatus.equalsIgnoreCase("PASS"))
                                                                        testCasePassStatusFlag++
                                                                    else if (testStepStatus.equalsIgnoreCase("FAIL"))
                                                                        testCaseFailStatusFlag++
                                                                }
                                                        }
                                                if(testCasePassStatusFlag>0 && testCaseFailStatusFlag<=0)
                                                    testCaseStatus="PASS"
                                                else if (testCaseFailStatusFlag>0)
                                                    testCaseStatus="FAIL"
                                                else
                                                    testCaseStatus="NO RUN"

                                                //SoapUI.log.info "Test Case Name : "+ tcName
                                                if(testCaseStatus.equalsIgnoreCase("PASS")||testCaseStatus.equalsIgnoreCase("FAIL"))
                                                    file.append("$projName,$tsName,$tcName,$testStepCount,$testCaseStatus,$currentUser\n")
                                            }
                                    }
                        }
                }
        UISupport.showInfoMessage("Artifacts Successfully exported!! Please see the SoapUI log for more information!","File Export Success!!!")
    }
}
/**
 * Created by Kondasamy J
 * Contact: Kondasamy@outlook.com
 */