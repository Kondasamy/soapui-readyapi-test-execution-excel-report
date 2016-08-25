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
        def today= new Date().format("yyyyMMdd")
        def fileName = today+"_SoapUI_Test_Results.csv"
        def mainDir = System.getProperty('user.home')
        def SubDir = "\\SoapUI Data\\"
        def SubDir1 = new File(mainDir,SubDir)
        if(SubDir1.exists())
        {
            def file = new File(SubDir1,fileName)
        }
        else
        {
            SubDir1.mkdirs()
            def file = new File(SubDir1,fileName)
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
                                        testCase.getTestStepsOfType(com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep.class).each
                                                {
                                                    tests->
                                                        def response = tests.httpRequest.response
                                                        if( response == null )
                                                        {
                                                            SoapUI.log.warn "Missing Response for TestStep : " + tests.testStep.testCase.testSuite.name + "=>"+tests.testStep.testCase.name+ "->"+tests.name
                                                        }
                                                        def data = response.getRawResponseData()
                                                        if( data == null || data.length == 0 )
                                                        {
                                                            SoapUI.log.warn "Empty Response data for TestStep : "+ tests.testStep.testCase.testSuite.name + "=>"+tests.testStep.testCase.name+ "->"+tests.name
                                                        }
                                                        else
                                                        {
                                                            def tstName = tests.getName()
                                                            SoapUI.log.info "***Raw Request and Raw Response is exported to a file :: ==>"+mainDir+ SubDir+"\\"+fileName

                                                        }

                                                }
                                        testCase.getTestStepsOfType(com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep.class).each
                                                {
                                                    tests->
                                                        def response = tests.httpRequest.response
                                                        if( response == null )
                                                        {
                                                            SoapUI.log.warn "Missing Response for TestStep : " + tests.testStep.testCase.testSuite.name + "=>"+tests.testStep.testCase.name+ "->"+tests.name
                                                            return
                                                        }
                                                        def data = response.getRawResponseData()
                                                        if( data == null || data.length == 0 )
                                                        {
                                                            SoapUI.log.warn "Empty Response data for TestStep : "+ tests.testStep.testCase.testSuite.name + "=>"+tests.testStep.testCase.name+ "->"+tests.name
                                                            return
                                                        }
                                                        else
                                                        {
                                                            def rawRequest = new String(response.getRawRequestData(),"UTF-8")
                                                            def rawResponse = new String(response.getRawResponseData(),"UTF-8")
                                                            def tstName = tests.getName()



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
