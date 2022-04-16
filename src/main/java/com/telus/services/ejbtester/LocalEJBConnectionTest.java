package com.telus.services.ejbtester;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.telus.applications.usage.dto.UsageEventDto;
//import com.telus.applications.usage.dto.UsageEventDto;
import com.telus.applications.usage.exception.InvalidEventTypeException;
import com.telus.applications.usage.services.unbilledeventservice.UnbilledEventServiceConfig;
import com.telus.applications.usage.services.unbilledeventservice.UnbilledEventServiceHome;
import com.telus.applications.usage.services.unbilledeventservice.UnbilledEventServiceRemote;
import com.telus.billing.domain.BilledUsageEvent;
import com.telus.billing.domain.BillingSummary;
import com.telus.billingcare.adjustment.service.ChargeSearchCriteriaDto;
import com.telus.billingcare.adjustment.service.ejb.AdjustmentTransactionMgtSvcEjbRHome;
import com.telus.billingcare.adjustment.service.ejb.AdjustmentTransactionMgtSvcEjbRemote;
import com.telus.billingcare.adjustment.service.ejb.BillingMgtSvcEjbRemote;
import com.telus.billingcare.adjustment.service.ejb.BillingMgtSvcEjbRhome;
import com.telus.billingcare.charge.service.ejb.ChargeMgtSvcEjbRHome;
import com.telus.billingcare.charge.service.ejb.ChargeMgtSvcEjbRemote;
import com.telus.billingcare.request.service.ejb.BillingRequestMgtSvcEjbRemote;
import com.telus.billingcare.request.service.ejb.BillingRequestMgtSvcEjbRhome;
import com.telus.billingcare.transaction.domain.BillingRequest;
import com.telus.billingcare.transaction.domain.BillingTransaction;
import com.telus.billingcare.transaction.domain.Charge;
import com.telus.businessObjects.TelusApplicationException;
import com.telus.businessObjects.TelusSystemException;
import com.telus.collections.misc.domain.AccountBalanceReport;
import com.telus.collections.misc.service.ejb.AccountBalanceReportSvcRemote;
import com.telus.collections.misc.service.ejb.AccountBalanceReportSvcRhome;
import com.telus.collections.treatment.service.TreatmentPreConditionException;
//import com.telus.collections.treatment.service.dto.CollectionAccountStatusDto;
import com.telus.collections.treatment.service.ejb.TreatmentSvcEjbRemote;
import com.telus.collections.treatment.service.ejb.TreatmentSvcEjbRhome;
import com.telus.contactcentermgt.ivrmgt.compsvc.AccessKeyDto;
import com.telus.contactcentermgt.ivrmgt.compsvc.AccountBalancesDto;
import com.telus.contactcentermgt.ivrmgt.compsvc.ejb.IvrCompSvcEjbRemote;
import com.telus.contactcentermgt.ivrmgt.compsvc.ejb.IvrCompSvcEjbRhome;
import com.telus.credit.domain.CreditProfile;
import com.telus.credit.service.ejb.CreditProfileMgtSvcEjbRemote;
import com.telus.credit.service.ejb.CreditProfileMgtSvcEjbRhome;
import com.telus.customermgt.customerods.CustomerSubscriptionExt;
import com.telus.customermgt.diary.domain.CustomerEvent;
import com.telus.customermgt.diary.service.EventSearchCriteriaDto;
import com.telus.customermgt.diary.service.ejb.CustomerDiaryMgtSvcEjbRemote;
import com.telus.customermgt.diary.service.ejb.CustomerDiaryMgtSvcEjbRhome;
import com.telus.customermgt.domain.Address;
import com.telus.customermgt.domain.BillMedia;
import com.telus.customermgt.domain.BillingAccount;
import com.telus.customermgt.domain.BillingArrangement;
import com.telus.customermgt.domain.BillingName;
//import com.telus.credit.domain.CreditProfile;
//import com.telus.credit.service.ejb.CreditProfileMgtSvcEjbRemote;
//import com.telus.credit.service.ejb.CreditProfileMgtSvcEjbRhome;
import com.telus.customermgt.domain.Customer;
import com.telus.customermgt.domain.CustomerIndividual;
import com.telus.customermgt.domain.CustomerSubscription;
import com.telus.customermgt.domain.ServiceInstance;
import com.telus.customermgt.domain.TaxExemption;
import com.telus.customermgt.service.ejb.BillingAccountMgtSvcEjbRemote;
import com.telus.customermgt.service.ejb.BillingAccountMgtSvcEjbRhome;
import com.telus.customermgt.service.ejb.CustomerMgtSvcEjbRemote;
import com.telus.customermgt.service.ejb.CustomerMgtSvcEjbRhome;
import com.telus.customerprofilemgt.domain.CustomerProfile;
import com.telus.customerprofilemgt.domain.IdentityCredential;
import com.telus.customerprofilemgt.domain.SubscriberProfile;
import com.telus.customerprofilemgt.domain.SubscriberResource;
import com.telus.customerprofilemgt.domain.SubscriberResourceConfigAlias;
import com.telus.customerprofilemgt.service.IdentityCredentialProfileDto;
import com.telus.customerprofilemgt.service.IdentityCredentialsRejectedException;
import com.telus.customerprofilemgt.service.ejb.CustomerIdentityProfileMgtSvcEjbRHome;
import com.telus.customerprofilemgt.service.ejb.CustomerIdentityProfileMgtSvcEjbRemote;
import com.telus.customerprofilemgt.service.ejb.SubscriberIdentityProfileConfigurationSvcEjbRHome;
import com.telus.customerprofilemgt.service.ejb.SubscriberIdentityProfileConfigurationSvcEjbRemote;
import com.telus.customerprofilemgt.service.ejb.SubscriberProfileConfigurationSvcEjbRHome;
import com.telus.customerprofilemgt.service.ejb.SubscriberProfileConfigurationSvcEjbRemote;
import com.telus.customerprofilemgt.service.ejb.SubscriberProfileOrderingSvcEjbRHome;
import com.telus.customerprofilemgt.service.ejb.SubscriberProfileOrderingSvcEjbRemote;
import com.telus.framework.crypto.impl.pilot.PilotCryptoImpl;
import com.telus.framework.exception.ObjectNotFoundException;
import com.telus.identity.teammember.domain.TeamMember;
import com.telus.identity.teammember.domain.TeamMemberResultSetTooLargeException;
import com.telus.identity.teammember.domain.TeamMemberSearchCriteriaDto;
import com.telus.identity.teammember.domain.TeamMemberServiceException;
import com.telus.identity.teammember.services.ejb.TeamMemberMgtServiceHome;
import com.telus.identity.teammember.services.ejb.TeamMemberMgtServiceRemote;
import com.telus.paymentgateway.exception.PaymentReversalException;
import com.telus.paymentgateway.reversal.service.dto.PaymentReversalRequestDto;
import com.telus.paymentgateway.reversal.service.dto.PaymentReversalResultDto;
import com.telus.paymentgateway.service.ejb.PaymentGatewaySvcEjbRemote;
import com.telus.paymentgateway.service.ejb.PaymentGatewaySvcEjbRhome;
import com.telus.productmanagement.domain.CatalogueItem;
import com.telus.productmanagement.service.ejb.ProductODSCacheSvcHome;
import com.telus.productmanagement.service.ejb.ProductODSCacheSvcRemote;
import com.telus.provisioning.address.svc.InvalidAddressException;
import com.telus.provisioning.address.svc.dto.resp.SearchAddressRespDto;
import com.telus.provisioning.address.svc.ejb.AddressSvcEjbRemote;
import com.telus.provisioning.address.svc.ejb.AddressSvcEjbRhome;
import com.telus.provisioning.domain.Province;
import com.telus.provisioning.domain.ServiceAddress;
//import com.telus.referenceods.exchangeinfo.domain.ExchangeForborneStatus;
//import com.telus.referenceods.exchangeinfo.service.ServiceClassRateConstant;
//import com.telus.identity.teammember.domain.Group;
//import com.telus.identity.teammember.domain.Role;
//import com.telus.identity.teammember.domain.TeamMemberProfile;
//import com.telus.identity.teammember.services.TeamMemberMgtService;
//import com.telus.identity.teammember.services.ejb.TeamMemberMgtServiceHome;
//import com.telus.identity.teammember.services.ejb.TeamMemberMgtServiceRemote;
//import com.telus.serviceconfig.service.ejb.CallingCardConfigSvcEjbRhome;
//import com.telus.referenceods.exchangeinfo.service.ejb.ExchangeInfoSvcEjbRemote;
//import com.telus.referenceods.exchangeinfo.service.ejb.ExchangeInfoSvcEjbRhome;
import com.telus.provisioning.domain.types.SearchAddressRespType;
import com.telus.referenceods.exchangeinfo.domain.ExchangeForborneStatus;
import com.telus.referenceods.exchangeinfo.service.ServiceClassRateConstant;
import com.telus.referenceods.exchangeinfo.service.ejb.ExchangeInfoSvcEjbRemote;
import com.telus.referenceods.exchangeinfo.service.ejb.ExchangeInfoSvcEjbRhome;
import com.telus.tca.businessObjects.CustomerAccountNumber;
import com.telus.tca.businessObjects.TelephoneNumber;

import amdocs.acmarch.exceptions.ACMException;
import amdocs.bl3g.exceptions.BillingException;
import amdocs.bl3g.sessions.interfaces.api.BL9ChargeServices;
import amdocs.bl3g.sessions.interfaces.home.BL9ChargeServicesHome;

//import com.telus.billingcare.adjustment.service.ejb.AdjustmentTransactionMgtSvcEjbRemote;
//import com.telus.billingcare.adjustment.service.ejb.AdjustmentTransactionMgtSvcEjbRHome;
//import com.telus.billingcare.transaction.domain.BillingRequest;


//import junit.framework.TestCase;

/**
 * This class performs calls to the local deployment of the Adjustment
 * Management Services
 * 
 * @author Hythum Hamid
 * 
 */
public class LocalEJBConnectionTest {

	// public AdjustmentTransactionMgtSvcEjbRemote m_adjustmentMgtSvc;
	// public AdjustmentTransactionMgtSvcEjbRemote m_adjustmentMgtSvc;
	public Context ctx;
	
	//can connect to t3://ln98422.ent.agt.ab.ca:41120 - working GTG! (without putting env security details in hashtable)
	//but not to t3://localhost:7001
	//com.telus.identity.services.TeamMemberMgtService
	
	//this is for BillingRequestMgtSvc in AT
	//t3://ln98422.ent.agt.ab.ca:41120
	//com.telus.billingcare.request.service.BillingRequestMgtSvc
	
	//usage AT01:
	//t3://sedm3207.ent.agt.ab.ca:23241, sedm3208.ent.agt.ab.ca:23241
	
	//com.telus.collections.treatment.service.PaymentArrangementMgtSvc AT01
	//t3://sedm3207.ent.agt.ab.ca:23241,sedm3208.ent.agt.ab.ca:23241
	
	//asfSvr
	//t3://lp97871.ent.agt.ab.ca:43312

	public static Hashtable<String, String> getLocalDeploymentContext() {

		Hashtable<String, String> env = new Hashtable<String, String>(5);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		//env.put(Context.PROVIDER_URL, "t3://sedm3216.ent.agt.ab.ca:21221");// ERASE ME prod
		//env.put(Context.PROVIDER_URL, "t3://ln98422.ent.agt.ab.ca:41120");//AT01 
		
		//env.put(Context.PROVIDER_URL, "t3://localhost:7001"); //ExchangeInforService @ billODSMgtSvr
		//env.put(Context.PROVIDER_URL, "t3://ln99028.ent.agt.ab.ca:41517,ln99029.ent.agt.ab.ca:41517,ln99084.ent.agt.ab.ca:41517,ln99085.ent.agt.ab.ca:41517");//ST01
		//env.put(Context.PROVIDER_URL, "t3://ln98888.ent.agt.ab.ca:41317,ln98887.ent.agt.ab.ca:41317");
		//env.put(Context.PROVIDER_URL, "t3://lp97871.ent.agt.ab.ca:41317,lp97872.ent.agt.ab.ca:41317,lp97873.ent.agt.ab.ca:41317,lp97874.ent.agt.ab.ca:41317");
		//env.put(Context.PROVIDER_URL, "t3://apppr.ent.agt.ab.ca:21073"); //Enabler PR (need uams spring setup)
		//env.put(Context.PROVIDER_URL, "t3://lp97874.ent.agt.ab.ca:41317,lp97873.ent.agt.ab.ca:41317,lp97871.ent.agt.ab.ca:41317,lp97872.ent.agt.ab.ca:41317"); // @ billingCare/ @customermgt
		
		//@ billingCareSvr
		//env.put(Context.PROVIDER_URL, "t3://lp97874.ent.agt.ab.ca:41317,lp97873.ent.agt.ab.ca:41317,lp97871.ent.agt.ab.ca:41317,lp97872.ent.agt.ab.ca:41317");
		//env.put(Context.PROVIDER_URL, "t3://lp97874.ent.agt.ab.ca:41317"); // @ billingCareSvr PR NODE 4
		
		//fusSvr eg addressSvc:
		//env.put(Context.PROVIDER_URL, "t3://lp97871.ent.agt.ab.ca:43312,lp97872.ent.agt.ab.ca:43312,lp97873.ent.agt.ab.ca:43312,lp97874.ent.agt.ab.ca:43312,lp97807.ent.agt.ab.ca:43312,lp97808.ent.agt.ab.ca:43312,lp97809.ent.agt.ab.ca:43312,lp97810.ent.agt.ab.ca:43312");
		//env.put(Context.PROVIDER_URL, "t3://lp97874.ent.agt.ab.ca:43312");
		
		//ExchangeInforService @ billODSMgtSvr
		//env.put(Context.PROVIDER_URL, "t3://lp97871.ent.agt.ab.ca:46311,lp97872.ent.agt.ab.ca:46311,lp97873.ent.agt.ab.ca:46311,lp97874.ent.agt.ab.ca:46311,lp97807.ent.agt.ab.ca:46311,lp97808.ent.agt.ab.ca:46311,lp97809.ent.agt.ab.ca:46311,lp97810.ent.agt.ab.ca:46311");
		//env.put(Context.PROVIDER_URL, "t3://lp97874.ent.agt.ab.ca:46311");
		
		//ticketSvr
		//t3://lp99378.corp.ads:50051,lp99379.corp.ads:50051,lp99380.corp.ads:50051,lp99381.corp.ads:50051
		//env.put(Context.PROVIDER_URL, "t3://lp99379.corp.ads:50051");
		
		//customerMgtSvr
		//t3://lp97874.ent.agt.ab.ca:41317,lp97873.ent.agt.ab.ca:41317,lp97871.ent.agt.ab.ca:41317,lp97872.ent.agt.ab.ca:41317
		//env.put(Context.PROVIDER_URL, "t3://lp97871.ent.agt.ab.ca:41317");
		
		//customerMgtSvr @AT01
		//t3://ln98422.ent.agt.ab.ca:41120,ln98423.ent.agt.ab.ca:41120
		//env.put(Context.PROVIDER_URL, "t3://ln98422.ent.agt.ab.ca:41120");
		
		//fusSvr @PT62
		//env.put(Context.PROVIDER_URL, "t3://ln98945.ent.agt.ab.ca:43742");
		
		
		//fusSvr @AT06
		//env.put(Context.PROVIDER_URL, "t3://ln98945.ent.agt.ab.ca:43742");
		
		/** t3://lp97871.ent.agt.ab.ca:46311,lp97872.ent.agt.ab.ca:46311,lp97873.ent.agt.ab.ca:46311,lp97874.ent.agt.ab.ca:46311, - @ usageSvr
		 * lp97807.ent.agt.ab.ca:46311,lp97808.ent.agt.ab.ca:46311,lp97809.ent.agt.ab.ca:46311,lp97810.ent.agt.ab.ca:46311 - @ usageSvr
		 */
		
		/**t3://lp97874.ent.agt.ab.ca:41317,lp97873.ent.agt.ab.ca:41317,lp97871.ent.agt.ab.ca:41317,lp97872.ent.agt.ab.ca:41317 - @ serviceConfigSvr and customerProfileMgtSvr
		 */	
		//env.put(Context.PROVIDER_URL, "t3://lp97874.ent.agt.ab.ca:41317");
		
		
		//ST01 @BillingCareSvr
		//env.put(Context.PROVIDER_URL, "t3://ln99028.ent.agt.ab.ca:41517");
		//t3://ln99028.ent.agt.ab.ca:41517,ln99029.ent.agt.ab.ca:41517,ln99084.ent.agt.ab.ca:41517,ln99085.ent.agt.ab.ca:41517
		
		//env.put(Context.PROVIDER_URL,"t3://lp97874.ent.agt.ab.ca:46311");
		
		//env.put(Context.PROVIDER_URL, "t3://ln98424.ent.agt.ab.ca:41120,ln98425.ent.agt.ab.ca:41120"); // AT06 @billingCare/ @customermgt
		//env.put(Context.PROVIDER_URL, "t3://ln98424.ent.agt.ab.ca:41120");
		
		//PT65 t3://ln98956.ent.agt.ab.ca:41247,ln98957.ent.agt.ab.ca:41247
		//env.put(Context.SECURITY_PRINCIPAL,"verification");
		//env.put(Context.SECURITY_CREDENTIALS, "verification") ;
		
		//fusSvr pr:
		//t3://sedm3216.ent.agt.ab.ca:21081,sedm3217.ent.agt.ab.ca:21081,sedm3218.ent.agt.ab.ca:21081,sedm3219.ent.agt.ab.ca:21081
		
		// env.put( Context.PROVIDER_URL, "t3://sedm3202:20001" );//D3 work
		
		// paymentGateway
		//env.put(Context.PROVIDER_URL, "t3://ln98422.ent.agt.ab.ca:46211,ln98423.ent.agt.ab.ca:46211");
		
		// Danee
		// env.put(Context.PROVIDER_URL, "t3://lp97874:41317"); //ln98438.ent.agt.ab.ca:41357,ln98439.ent.agt.ab.ca:41357");
		// IVR EJB
		// env.put(Context.PROVIDER_URL, "t3://ln98424.ent.agt.ab.ca:41120,ln98425.ent.agt.ab.ca:41120");
		//env.put(Context.PROVIDER_URL, "t3://ln99028.ent.agt.ab.ca:41517,ln99029.ent.agt.ab.ca:41517,ln99084.ent.agt.ab.ca:41517,ln99085.ent.agt.ab.ca:41517");
		// local
		//env.put(Context.PROVIDER_URL, "t3://localhost:7003");
		
		// PT05
		//env.put(Context.PROVIDER_URL, "t3://ln98438.ent.agt.ab.ca:41357,ln98439.ent.agt.ab.ca:41357");
		env.put(Context.PROVIDER_URL, "t3://lp97874.ent.agt.ab.ca:41317,lp97873.ent.agt.ab.ca:41317,lp97871.ent.agt.ab.ca:41317,lp97872.ent.agt.ab.ca:41317");
		return env;
	}
	
	//from http://www.coderanch.com/t/461010/EJB-JEE/java/JNDI-ejb
    public static void showJndiProp(InitialContext ctx) throws NamingException {  
        NameClassPair element;  
        try {  
            System.err.println("---------------------------------------------");  
            System.out.println("Context Names:");  
            NamingEnumeration<NameClassPair> ne = ctx.list("");  
            while (ne.hasMore()) {  
                element = ne.next();  
                
                System.out.println("Name: " + element.getName());  
                //System.out.println("Name2 " + element.getNameInNamespace());

  
            }  
            System.out.println("---------------------------------------------");  
  
        } catch (NamingException namingException) {  
            namingException.printStackTrace();  
        }  
    }  
    
    //from http://stackoverflow.com/questions/15112590/get-the-class-instance-variables-and-print-their-values-using-reflection TG!
    public void printFields(Object obj) throws Exception {
        Class<?> objClass = obj.getClass();
    	System.out.println("here");
        Field[] fields = objClass.getFields();
        for(Field field : fields) {
            String name = field.getName();
            Object value = field.get(obj);
            System.out.println(name + ": " + value.toString());
        }
    }


	public static void main(String[] args) throws NamingException, RemoteException, CreateException, ObjectNotFoundException,  TreatmentPreConditionException, InvalidEventTypeException, PaymentReversalException, TelusSystemException, TelusApplicationException, TeamMemberServiceException, TeamMemberResultSetTooLargeException, ParseException {
		//new LocalEJBConnectionTest().testExchangeInfoService();
		//new LocalEJBConnectionTest().testBillingAccountMgtSvc();
		//new LocalEJBConnectionTest().testSubscriberProfileOrderingSvc();
		//new LocalEJBConnectionTest().testMe13();
		//new LocalEJBConnectionTest().testBillingAccountMgtSvc();
		//new LocalEJBConnectionTest().testPgwEjb();
		//new LocalEJBConnectionTest().testIVR();
		//LocalEJBConnectionTest.testTeamMemberService();
		new LocalEJBConnectionTest().testBillingMgtSvc();
	}
	
	private static void testTeamMemberService() throws NamingException, RemoteException, CreateException, TeamMemberServiceException, TeamMemberResultSetTooLargeException {
		Context ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		System.out.println(ctx.lookup("com.telus.identity.services.TeamMemberMgtService"));
		TeamMemberMgtServiceHome rHome = (TeamMemberMgtServiceHome) ctx.lookup("com.telus.identity.services.TeamMemberMgtService");
		TeamMemberMgtServiceRemote remote = rHome.create();
		
		TeamMemberSearchCriteriaDto criteria = new TeamMemberSearchCriteriaDto();
		criteria.setTeamMemberUserId("t000001");
		
		TeamMember[] result = remote.findTeamMember(criteria);
		for (TeamMember tm : result) {
			System.out.print("ID: ");
			System.out.println(tm.getTeamMemberID());
		}
	}

	private void testIVR() throws NamingException, RemoteException, ObjectNotFoundException, CreateException, PaymentReversalException, TelusSystemException, TelusApplicationException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		System.out.println(ctx.lookup("com.telus.contactcentermgt.ivrmgt.compsvc.IvrCompSvc"));
		IvrCompSvcEjbRhome rHome = (IvrCompSvcEjbRhome) ctx.lookup("com.telus.contactcentermgt.ivrmgt.compsvc.IvrCompSvc");
		IvrCompSvcEjbRemote remote = rHome.create();
		
		AccessKeyDto akey = new AccessKeyDto();
		akey.setCustomerId(0);
		akey.setServiceInstanceId(0);
		akey.setTelephoneNumber(new TelephoneNumber("7803322599"));
		akey.setTelephoneNumberAvailable(true);
		akey.setCustomerAccountNumber(new CustomerAccountNumber(604951533));
		akey.setClassOfService("R");
		akey.setBillingTypeCode("01");
		System.out.println(akey);
		AccountBalancesDto item = remote.getAccountBalancesInfo(akey);
		System.out.println(item);
	}
	
	private void testPODS() throws NamingException, RemoteException, ObjectNotFoundException, CreateException, PaymentReversalException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		System.out.println(ctx.lookup("com.telus.productmanagement.service.ejb.ProductODSCacheSvcHome"));
		ProductODSCacheSvcHome rHome = (ProductODSCacheSvcHome) ctx.lookup("com.telus.productmanagement.service.ejb.ProductODSCacheSvcHome");
		ProductODSCacheSvcRemote remote = rHome.create();
		
		CatalogueItem item = remote.getCatalogueItem(1256102L);
		System.out.println(item);
	}
	
	private void testPgwEjb() throws NamingException, RemoteException, ObjectNotFoundException, CreateException, PaymentReversalException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		PaymentGatewaySvcEjbRhome rHome = (PaymentGatewaySvcEjbRhome) ctx.lookup("com.telus.paymentgateway.service.PaymentGatewaySvc");
		PaymentGatewaySvcEjbRemote remote = rHome.create();
		
		String tpid = "911034550000001102005-11-28";
        String csrUserId = "123";
		String reversalremarks = "reversalComment";
        int callerSysId = 101;
        PaymentReversalRequestDto dto = new PaymentReversalRequestDto();
		dto.setPaymentId(tpid);                         
		dto.setCallerSystemId(new Integer(callerSysId));
		dto.setUserId(csrUserId);                       
		dto.setRemark(reversalremarks);                 
		dto.setAdjustmentReasonCode("abc");             
		PaymentReversalResultDto[] response = remote.reversePayment(dto);
	}

	//working ChargeMgtSvc in PR for unbilled usage.. TG..

	private void testCustomerDiarySvc() throws NamingException, RemoteException, ObjectNotFoundException, CreateException {
		
		//customerMgtSvr
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		CustomerDiaryMgtSvcEjbRhome rHome = (CustomerDiaryMgtSvcEjbRhome) ctx.lookup("com.telus.customermgt.diary.service.CustomerDiaryMgtSvc");
		CustomerDiaryMgtSvcEjbRemote remote = rHome.create();
		
		CustomerEvent customerEvent = new CustomerEvent();
		customerEvent.setUserId("t000011");
		customerEvent.setDateTime(Calendar.getInstance().getTime());
		customerEvent.setSourceId(1003);
		customerEvent.setSystemGenerated( false );
		customerEvent.setComment("Testing 101");
		customerEvent.setHighlighted(true);
		customerEvent.setRetentionCode("Perm");
		customerEvent.setReferenceEntitySourceId(1001);
		//customerEvent.setReferenceEntityId("603229238");
		customerEvent.setReferenceEntityTypeCode("BACCT");
		customerEvent.setCustomerId(120637212L);
		
		customerEvent.setEventTypeId(4l);
		
		remote.createCustomerEvent(customerEvent, null);
		System.out.println("Done calling create customer event");
		
		EventSearchCriteriaDto searchCriteria = new EventSearchCriteriaDto();
		  searchCriteria.setCustomerId(120637212L);
		  searchCriteria.setOrderByHighlighted(true);
		  //searchCriteria.setLanguageCode("EN");
		  //long[] type = {CustomerEventTypeConstants.MANUAL_FORCED_PROVISIONING};
          //String[] eventCategories = {CustomerEventTypeConstants.CATEGORY_ORDER, 
          //										   CustomerEventTypeConstants.CATEGORY_FORCEDPROV};
        //searchCriteria.setEventCategories(eventCategories);
        //searchCriteria.setEventTypes(type);
		searchCriteria.setReferenceEntityId("603229238");
		searchCriteria.setReferenceEntityTypeCode("BACCT");
        int days = 180;
        Calendar calendar = Calendar.getInstance();
		  calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - days);
        
        CustomerEvent[] eventsDO  = remote.findCustomerEvents(searchCriteria);
		for(CustomerEvent event: eventsDO){
			if(event.isHighlighted() || 
	  				  event.getDateTime().compareTo(calendar.getTime()) >= 0){
				System.out.println("-------------------------------------");
				System.out.println("Highlighted or Priority: "+event.isHighlighted());
				System.out.println("Category: "+event.getCategoryCode());
				System.out.println("Date Time: "+event.getDateTime());
				System.out.println("Comment: "+event.getComment());
				System.out.println("User Id: "+event.getUserId());
			}
		}
          

	}
	
	private void testAddressSvc() throws NamingException, RemoteException, ObjectNotFoundException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		AddressSvcEjbRhome rHome = (AddressSvcEjbRhome) ctx.lookup("com.telus.billingcare.adjustment.service.BillingMgtSvc");
		AddressSvcEjbRemote remote = rHome.create();
		
	}
	
	private void testBillingMgtSvc() throws NamingException, RemoteException, ObjectNotFoundException, CreateException, ParseException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		BillingMgtSvcEjbRhome rHome = (BillingMgtSvcEjbRhome) ctx.lookup("com.telus.billingcare.adjustment.service.BillingMgtSvc");
		BillingMgtSvcEjbRemote remote = rHome.create();
		
		
		
		//234283661L, 234283661L
		/*BillDocumentSummary[] billDocuments = remote.getBillDocumentSummaries(234283661L, 234283661L);
		BilledUsageEvent[] events = null;
		for(BillDocumentSummary summary: billDocuments){
			System.out.println("-------------------------------------");
			System.out.println("Bill Date: "+summary.getBillDate());
			System.out.println("Bill Document Id: "+summary.getBillDocumentId());
			
		}*/
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		try {
			//bill date change your local time zone to PT
			Date date = dateFormat.parse("2019-05-22");
			BillingSummary billingSummary = remote.getBillingSummary(217438875L, 600013956L, date, "CAD");
			
			System.out.println("-------------------------------------");
			System.out.println("Billing Name: "+billingSummary.billingName());
			System.out.println("Billing Date: "+billingSummary.getBillDate());
			System.out.println("Total Due Amount: "+billingSummary.getTotalDueAmount().toBigDecimal().doubleValue());
			
		
		}
		catch(ObjectNotFoundException e){
			//do nothing
			System.out.println("Object Not Found");
		} catch(ParseException e){
			System.out.println("Date invalid");
		}
		
		
		// Asing
		ChargeSearchCriteriaDto dto = new ChargeSearchCriteriaDto();
		dto.setServiceInstanceId(733918418);
		dto.setBillId(1133785623);
		//BilledUsageEvent[] bues = remote.findBilledUsageEvents(dto,new Date(2022, 03,07));
		BilledUsageEvent[] bues = remote.findBilledUsageEvents(dto,dateFormat.parse("2022-03-07"));
		System.out.println(bues.length);
		
		for (BilledUsageEvent bue : bues) {
			System.out.println(bue.getBillDocumentId());
		}
		

	}
	
	private void testSubscriberIdentityProfileConfigSvc() throws NamingException, RemoteException, ObjectNotFoundException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		SubscriberIdentityProfileConfigurationSvcEjbRHome rHome = (SubscriberIdentityProfileConfigurationSvcEjbRHome) ctx.lookup("com.telus.customerprofilemgt.service.SubscriberIdentityProfileConfigurationSvc");
		SubscriberIdentityProfileConfigurationSvcEjbRemote remote = rHome.create();
		
		IdentityCredentialProfileDto[] identityCredentialProfiles = remote.getActiveIdentityCredentialProfilesByServiceInstanceId(606586279L);
		
		for(IdentityCredentialProfileDto credProf: identityCredentialProfiles){
			System.out.println("-------------------------------------");
			
			IdentityCredential[] credentials = credProf.getCredentials();
			for(IdentityCredential cred: credentials){
				System.out.println("Prod Type Code: "+credProf.getServiceTypeCode());
				System.out.println("Resource Id: "+credProf.getResourceId());
				System.out.println("Resouce Val Id: "+credProf.getServiceResourceValueId());
				System.out.println("Username: "+cred.getKey());
				System.out.println("Identity Credential Id: "+cred.getId());
			}
			
		}

	}
	
	private void testUsageEventSvc() throws NamingException, RemoteException, ObjectNotFoundException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		UnbilledEventServiceHome rHome = (UnbilledEventServiceHome) ctx.lookup("com.telus.service.UnbilledEventService");
		UnbilledEventServiceRemote remote = rHome.create();
		
		UnbilledEventServiceConfig unbilledEventServiceConfig = new UnbilledEventServiceConfig();
		unbilledEventServiceConfig.setServiceInstanceId(613391745L);
		unbilledEventServiceConfig.setEventType("Adsl");
		unbilledEventServiceConfig.setSortColumn("EVENT_START_TS DESC");
		unbilledEventServiceConfig.setStartingRowNumber(1);
		unbilledEventServiceConfig.setEndingRowNumber(21);
		
		UsageEventDto[] usageEvents = {};
		
		try {
			usageEvents = remote.getUnbilledEvents(unbilledEventServiceConfig);
		} catch (InvalidEventTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(UsageEventDto usg: usageEvents){
			System.out.println("-------------------------------------");
			System.out.println("Event ID: "+usg.getUsageEventId());
			System.out.println("Service Instance ID: "+usg.getServiceInstanceId());
			System.out.println("Net Charge: "+usg.getNetChargeAmount());
			System.out.println("Event Duration Quantity: "+usg.getEventDurationQuantity());
			System.out.println("Start Date: "+usg.getEventStartTimestamp());
			System.out.println("End Date: "+usg.getEventEndTimestamp());
		}
		
		//normal customer_id = 90187564
		//can't search in DT1 and if search in quick no billing accounts showing in billing accounts area customer_id = 98452923
		//CreditProfileDto[] creditProfileDtoList = remote.searchCreditMgtByCustomerId(98452923);
		
		//System.out.println("Credit Profile Length: "+creditProfileDtoList.length);
		System.out.println("-------------------------------------");

	}
	
	private void testBillingRequestMgtSvc() throws NamingException, RemoteException, ObjectNotFoundException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		BillingRequestMgtSvcEjbRhome rhome = (BillingRequestMgtSvcEjbRhome) ctx.lookup("com.telus.billingcare.request.service.BillingRequestMgtSvc");
		BillingRequestMgtSvcEjbRemote remote = rhome.create();
		
		BillingRequest billingRequest = remote.getBillingRequest(23506126L);
		
		BillingTransaction[] trans = billingRequest.getBillingTransactions();
		System.out.println("----------------------------------------------------");
		System.out.println("BAN: "+billingRequest.getBillingAccountNumber());
		System.out.println("Billing Request Status: "+billingRequest.getStatus());
		for(BillingTransaction br: trans){
			System.out.println("###### Billing Transaction #######");
			System.out.println("Billing Request Id: "+br.getBillingRequestId());
			System.out.println("Type: "+br.getType());
			System.out.println("Usage Adjustment Reason Code: "+br.getUsageAdjustmentReasonCode());
			System.out.println("##################################");
		}
		
		
		//search billing request:
		/*BillingRequestSearchCriteriaDto search = new BillingRequestSearchCriteriaDto();
		search.setAssignedToTeamMemberId("T811227");
		search.setType(null);
		search.setStatus(null);
		BillingRequest[] billingRequests = remote.findBillingRequests(search, true);
		
		String reqExistMsg = billingRequests.length==0?"None":"Yes";
		System.out.println("Is Request Existing? "+reqExistMsg);
		
		String sameRequestId = "";
		for(BillingRequest req: billingRequests){
			if(!"RefundPayment".equals(req.getType())){
				System.out.println("Not this");
			}
			else{
				System.out.println("This is it!");
				System.out.println("----------------------------------------------------");
				System.out.println("Is Refund Payment? "+req.isReturnPayment());
				System.out.println("Amount: "+req.getAmount());
				System.out.println("Adj Type Code: "+req.getUsageAdjustmentReasonCode());
				System.out.println("Type: "+req.getType());
				System.out.println("Originator: "+req.getAssignedByTeamMemberId());
				System.out.println("BAN: "+req.getBillingAccountNumber());
				System.out.println("Status: "+req.getStatus());
				BillingTransaction[] trans = req.getBillingTransactions();
				for(BillingTransaction br: trans){
					sameRequestId = br.getBillingRequestId()!=23258194L?"":"#######################--OK";
					System.out.println("Is Adj Transaction? "+br.isAdjustmentTransaction());
					System.out.println("Billing Request ID: "+br.getBillingRequestId()+sameRequestId);
					System.out.println("Adj Type Code: "+br.getUsageAdjustmentReasonCode());
				}
			}
		}*/

	}
	
	private void testCustomerProfileMgt() throws NamingException, RemoteException, ObjectNotFoundException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		CreditProfileMgtSvcEjbRhome rHome = (CreditProfileMgtSvcEjbRhome) ctx.lookup("com.telus.credit.service.CreditProfileService");
		CreditProfileMgtSvcEjbRemote remote = rHome.create();
		
		//normal customer_id = 90187564
		//can't search in DT1 and if search in quick no billing accounts showing in billing accounts area customer_id = 98452923
		/*CreditProfileDto[] creditProfileDtoList = remote.searchCreditMgtByCustomerId(98452923);
		
		System.out.println("Credit Profile Length: "+creditProfileDtoList.length);
		System.out.println("-------------------------------------");*/
		
		System.out.println("-------------------------------------");
		com.telus.credit.domain.CreditProfile creditProfile = remote.getCreditProfile(31129166);
		System.out.println("C.Profile Id: "+creditProfile.get_id());
		System.out.println("Employment Status: "+creditProfile.getEmploymentStatusCode());
		System.out.println("C.Profile Status: "+creditProfile.getStatus());

	}

	private void testGetCustomer() throws NamingException, RemoteException, ObjectNotFoundException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		CustomerMgtSvcEjbRhome rHome = (CustomerMgtSvcEjbRhome) ctx.lookup("com.telus.customermgt.service.CustomerMgtService");
		CustomerMgtSvcEjbRemote remote = rHome.create();
		
		//BAN = 602857857
		//Customer customer = remote.getCustomer(11228749);
		//First cid = 93194539 (linked)
		//2nd cid in the list = 93195499 (open customer)
		CustomerIndividual cust = (CustomerIndividual) remote.getCustomer(31129166);
		String isNull = cust!=null?"No":"Yes, it is null";
		System.out.println("Is customer null? "+ isNull);
		System.out.println("Contact individual size: "+cust.getContactIndividuals().length);
		System.out.println("Pref Lang: "+cust.getPreferredLanguageCode());
		System.out.println("Pref Contact Method: "+cust.getPreferredContactMethodCode());
		System.out.println("Pref Contact Time: "+cust.getPreferredContactTimePeriodCode());
		
		ServiceInstance[] serviceIntances = cust.getServiceInstances();
		
		for(ServiceInstance si : serviceIntances){
			System.out.println("-------------------------------------");
			System.out.println("Product Type: "+si.getTypeCode());
			System.out.println("Primary Service Resource Type Code: "+si.getPrimaryServiceResourceTypeCode());
		}
		
		/*TaxExemption[] taxExemptions = remote.getCustomerTaxExemptions(20630974);
		
		for(TaxExemption tax: taxExemptions){
			System.out.println("-------------------------------------");
			System.out.println("External Id: "+tax.getExternalId());
			System.out.println("Entity: "+tax.getExemptionEntity());
			System.out.println("Exempt Num: "+tax.getExemptionNumber());
			System.out.println("Ref Num: "+tax.getExemptionReferenceNumber());
			System.out.println("Province: "+tax.getProvinceCode());
			System.out.println("Effective Date: "+tax.getExemptionEffectiveDate());
			System.out.println("Expiry Date: "+tax.getExemptionExpiryDate());
			
		}*/
		
		System.out.println("-------------------------------------");

	}
	
	private void testAccountBalance() throws NamingException, RemoteException, ObjectNotFoundException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		AccountBalanceReportSvcRhome rHome = (AccountBalanceReportSvcRhome) ctx.lookup("com.telus.collections.misc.service.AccountBalanceReportService");
		AccountBalanceReportSvcRemote remote = rHome.create();
		
		//BAN = 601636975
		AccountBalanceReport abr = remote.getAccountBalanceReport(601636975);
		System.out.println("Overdue Balance: "+abr.getOverdueBalance());
	}
	
	//061614
	private void testMe15() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		ChargeMgtSvcEjbRHome rHome = (ChargeMgtSvcEjbRHome)ctx.lookup("com.telus.billingcare.charge.service.ChargeMgtSvc");
		ChargeMgtSvcEjbRemote remote = rHome.create();

		try {
		Charge[] chg = (Charge[]) remote.getUnbilledCharges(1509495, 200646487, 200646487, 0);
		System.out.println(chg.length);
		} catch (RemoteException re) {
			re.printStackTrace();
			//re.toString();
		}
		
		//This is sample from Terry
		Charge[] chg = (Charge[]) remote.getUnbilledCharges(19369523, 601517100, 601843802, 0);
		System.out.println(chg.length);
	}
	
	
	//this is a working khaled tester.. TG!
	private void testMe14() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		CustomerIdentityProfileMgtSvcEjbRHome rHome = (CustomerIdentityProfileMgtSvcEjbRHome)ctx.lookup("com.telus.customerprofilemgt.service.CustomerIdentityProfileMgtSvc");
		CustomerIdentityProfileMgtSvcEjbRemote remote = rHome.create();
		
		//Actually, we don�t have credentials for this production user. 
		//I am using SOAPUI to call the service directly  (CustomerIdentityProfileMgtSvc) using  �getCustomerProfileByCustomerId�.
		//CustomerProfile cp = remote.getCustomerProfileByCustomerId(31300699);
		CustomerProfile cp = remote.getCustomerProfileByCustomerId(29150576);		
		SubscriberProfile[] sbArr = getApplicableSubscriberProfiles(cp.getSubscriberProfiles());
		
		for (SubscriberProfile subscriberProfile : sbArr) {			
			System.out.println(subscriberProfile.getResource().getId() + " " + subscriberProfile.getResource().getServiceTypeCode() + " " + subscriberProfile.getResource().getStatusCode() + " " + subscriberProfile.getResource().getServiceResourceValueId() + " " + subscriberProfile.getResource().getServiceInstanceId());
		}
	}
	
	//from Oliver (Consumer Portal)
	public static SubscriberProfile[] getApplicableSubscriberProfiles(SubscriberProfile[] subscriberProfiles) {

		if (subscriberProfiles == null || subscriberProfiles.length == 0) {
			return null;
		}

		ArrayList result = new ArrayList();

		for (int subscriberProfileIndex = 0;

		subscriberProfileIndex < subscriberProfiles.length;

		subscriberProfileIndex++) {

			if (getIsApplicableSubscriberProfile(

			subscriberProfiles[subscriberProfileIndex])) {

				result.add(subscriberProfiles[subscriberProfileIndex]);

			}

		}

		if (result.isEmpty())

		{
			return null;
		}

		return (SubscriberProfile[]) result.toArray(new SubscriberProfile[0]);

	}

	//from Oliver (Consumer Portal)
	public static boolean getIsApplicableSubscriberProfile(
	SubscriberProfile subscriberProfile)
	{
		return subscriberProfile != null &&	subscriberProfile.getResource() != null && "A".equals(
				subscriberProfile.getResource().getStatusCode());
	}

	
	
	//SubscriberProfileConfigurationSvc
	//Related to QC6841
	private void testMe13() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		SubscriberProfileConfigurationSvcEjbRHome rHome = (SubscriberProfileConfigurationSvcEjbRHome)ctx.lookup("com.telus.customerprofilemgt.service.SubscriberProfileConfigurationSvc");
		SubscriberProfileConfigurationSvcEjbRemote remote = rHome.create();
		
		/*
		 * this sample is from ate tin.. TG!
		MasterRequestRef: 149464320A
		MasterRequestSourceId: 1002
		ServiceResourceValueId: 20004849613
		ServiceTypeCode: WEBS
		*/
		
		/*
		THis is from the defect:
			for the samples from Anish:
			Order Number: 325413573A
			Resource Id: 20013188658
			Product: Satellite TV (STV)
			TN/PIN: ?????

			Order Number: 325970446A 
			Resource Id: 20006934502
			Product: Satellite TV (STV)
			TN/PIN: 4037523365

			Also, tested SubscriberProfileConfigurationSvc findSubscriberProfilesByServiceTypeAndValueId and was able to obtain the Future version state code.
	                MasterRequestRef = 256455764A;
	                MasterRequestSourceId = 1002;
	                ServiceResourceValueId = 20013188658;
	                setServiceTypeCode = STV;
	                
	                
	      From Joon Hee:
	      1) Provide Part Of Move
			Request:
			serviceResourceTypeCode:STV
			resourceID: 20012671446
			masterRequestRef: 327067442A
			Timestamp: 01/26/2014 23:05:57
			Response: Jeremie to provide on Feb 17
			
			2) Change Part of Replace Offer
			Request:
			serviceResourceTypeCode:STV
			resourceID: 20008212599
			masterRequestRef: 327711543A
			Timestamp: 01/28/2014 13:44:06
		
		Ate Tin
		MasterRequestRef: 149464320A
		MasterRequestSourceId: 1002
		ServiceResourceValueId: 20004849613
		ServiceTypeCode: WEBS
		
		From Joon:
		
		1)	Please check the request below and provide us with the response
		Order type: Inflight Change of Provide STV
		Request:
		�	serviceResourceTypeCode:STV
		�	resourceID: 20014645968
		�	masterRequestRef: 281155486B
		�	Timestamp: 04/01/2013 23:04:22


		/New item from Joon - 031714
		Provide Part Of Move
		Request:
		serviceResourceTypeCode:STV
		resourceID: 20010985006
		masterRequestRef: 331123691A
		
		when resource is Active, OARN is not mandatory
		BUT when resource is Pending,it is mandatory (the oarn saved in IPC + master src id)
	      
        */
		
		//this below is a working sample
		//SubscriberResource sr = new SubscriberResource();
		//sr.setMasterRequestRef("334548766A"); 
		//sr.setMasterRequestSourceId(1002);
		//sr.setServiceResourceValueId("20017705478");
		//sr.setServiceTypeCode("EM");
		
		
		SubscriberResource sr = new SubscriberResource();
		//sr.setMasterRequestRef("143780905A"); 
		//sr.setMasterRequestSourceId(1002);
		sr.setServiceResourceValueId("20003235269");
		sr.setServiceTypeCode("STV");
		//SubscriberProfile[] sProfileOrig = remote.findSubscriberProfilesByServiceType(arg0, arg1); //to update
		/*
		This one is for QC25724 - NC - Change Ownership Order does not send email address/alias to COS system causing customers to lose their email
		serviceResourceTypeCode:Email
		resourceID: 20004216593
		masterRequestRef: 347266333A
		*/
		
		//if no oarn anf master request source id is passed, can return FP
		//SubscriberResource sr = new SubscriberResource();
		//sr.setMasterRequestRef("255796422A"); //need to provide oarn
		//sr.setMasterRequestSourceId(1002);
		//sr.setServiceResourceValueId("20011497753");
		//sr.setServiceTypeCode("STV");
		
		//143780905B
		//143780905A
		
//		MasterRequestRef = 256455764A;
//		MasterRequestSourceId = 1002;
//		ServiceResourceValueId = 20013188658;
//		setServiceTypeCode = STV;
		SubscriberProfile[] sProfile = remote.findSubscriberProfilesByServiceTypeAndValueId(new SubscriberResource[]{sr});
		//Run in debug mode to get all attributes
		System.out.println("The number of subscriber profile obtained: " + sProfile.length);
		//System.out.println("The version state code is: " + sProfile[0].getResource().getOrderAliases()[0].getVersionStateCode());
		System.out.println("The resource id is: " + sProfile[0].getResourceId());
		//jem test: get the config aliases
		SubscriberResourceConfigAlias[] srca = sProfile[0].getResource().getConfigAliases();
		if (srca.length > 0) {
			System.out.println("srca count is : " + srca.length);
			System.out.println("srca value [0]: " + srca[0].getValue());
			System.out.println("srca value [0] status code: " + srca[0].getStatusCode());
			System.out.println("srca value [1]: " + srca[1].getValue());
			System.out.println("srca value [1] status code: " + srca[1].getStatusCode());
		}
		System.out.println("The identities count is: " + sProfile[0].getIdentities().length);
		System.out.println("The credentials count is: " + sProfile[0].getIdentities()[0].getCredentials().length);
		System.out.println("The credential id [0] is: " + sProfile[0].getIdentities()[0].getCredentials()[0].getId());
		System.out.println("The credential id [0] is: " + sProfile[0].getIdentities()[0].getCredentials()[0].getTypeCode());
		System.out.println("The credential id [0] is: " + sProfile[0].getIdentities()[0].getCredentials()[0].getVersionStateCode());
//		
//		System.out.println("The credential id [1] is: " + sProfile[0].getIdentities()[1].getCredentials()[0].getId());
//		System.out.println("The credential id [1] is: " + sProfile[0].getIdentities()[1].getCredentials()[0].getTypeCode());
//		System.out.println("The credential id [1] is: " + sProfile[0].getIdentities()[1].getCredentials()[0].getVersionStateCode());
		
		
		//IdentityCredentials is not null nor equal to ""
		try {
			this.printFields(sProfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//[0].getVersionStateCode()
		
		
		System.out.println("done jem");
		
	}
	
	
	
	
	//QC 26974 escalation - investigation completed - TG!
	private void testBillingAccountMgtSvc() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx); //amdocsBeans.BL9ChargeServicesBean
																											//below is jndi name from xml
		BillingAccountMgtSvcEjbRhome customerMgtSvcEjbRhome = (BillingAccountMgtSvcEjbRhome) ctx.lookup("com.telus.customermgt.service.BillingAccountMgtService");
		BillingAccountMgtSvcEjbRemote billingAccountMgtSvcEjbRemote = customerMgtSvcEjbRhome.create();
		
		/*BillingAccount ba = billingAccountMgtSvcEjbRemote.getBillingAccount(230799426);
		
		System.out.println("Pay Channel: "+ba.getBillingArrangements()[0].getPayChannel().getPayChannelNumber());
		System.out.println("Status Code: "+ba.getStatusCode());
		System.out.println("Current Balance Amount: "+ba.getCurrentBalanceAmount());
		
		Address address = ba.getBillingAddress();
		System.out.println("Address ID: "+address.getAddressId());
		System.out.println("Address Type Code: "+address.getAddressTypeCode());
		System.out.println("-------------------------------------------");
		*/
		
		
		BillingAccount ba = billingAccountMgtSvcEjbRemote.getBillingAccountFromMaster(208517762);
		//System.out.println("Test: "+ba.getBillingAccountNumber());
		System.out.println("-------------------------------------------");
		BillingName[] bnames = ba.getBillingNames();
		for(BillingName bn: bnames){
			System.out.println("First Name: "+bn.getFirstName());
			System.out.println("Last Name: "+bn.getLastName());
		}
		
		BillingArrangement[] arrangements = ba.getBillingArrangements();
		for(BillingArrangement arr: arrangements){
			BillMedia[] billMedias = arr.getBillMedias();
			for(BillMedia billMedia: billMedias){
				System.out.println("Bill Media: "+billMedia.getMediaTypeCode());
			}
		}
		
		Address ebill = ba.getAddressByType("E");
		
		System.out.println("Email Add: "+ebill.getEmailAddressText());
		
		
		/*//from OP: passing this: 85488065 returns this as exception: 85487806
		BillingAccount[] ba = billingAccountMgtSvcEjbRemote.getBillingAccountsByCustomerId(97026690);
		System.out.println(ba.length);
		BillingAccount ba1 = billingAccountMgtSvcEjbRemote.getBillingAccount(603536023);
		
		try {
			billingAccountMgtSvcEjbRemote.updateBillingAccount(ba1);
		} catch (ConcurrencyConflictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoAttributesModifiedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	
	private void testMe11() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
		//jndi is com.telus.customermgt.service.CustomerMgtService
		//this uses CustomerMgtSvr in env.xml
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx); //amdocsBeans.BL9ChargeServicesBean
		CustomerMgtSvcEjbRhome customerMgtSvcEjbRhome = (CustomerMgtSvcEjbRhome) ctx.lookup("com.telus.customermgt.service.CustomerMgtService");
		CustomerMgtSvcEjbRemote customerMgtSvcEjbRemote = customerMgtSvcEjbRhome.create();
		
		Customer requestedCustomer = customerMgtSvcEjbRemote.getCustomerByBillingAccountNumber(600871743);
		
		
		//needs telus-bi-domain-jdk1.6.jar  -working below TG!
		/*
		Customer forOPTest = customerMgtSvcEjbRemote.getCustomerEligibilityInfo(46602067); //getCustomerEligibilityInfo
		System.out.println(forOPTest.getAddress().getAddressId());
		System.out.println(forOPTest.getAddress().getAddressTypeCode());
		System.out.println(forOPTest.getAddress().getCivicNumber());
		System.out.println(forOPTest.getAddress().getUnitNumber());
		System.out.println(forOPTest.getAddress().getStreetName());
		System.out.println(forOPTest.getAddress().getAddressLine1());
		System.out.println(forOPTest.getAddress().getAddressLine2());
		System.out.println(forOPTest.getAddress().getAddressLine3());
		System.out.println(forOPTest.getAddress().getAddressLine4());
		System.out.println(forOPTest.getAddress().getAdditionalAddressInformation1());
		System.out.println(forOPTest.getAddress().getAdditionalAddressInformation2());
		System.out.println(forOPTest.getAddress().getAdditionalAddressInformation3());
		System.out.println(forOPTest.getAddress().getAdditionalAddressInformation4());
		*/
		
		
		TaxExemption[] ts = customerMgtSvcEjbRemote.getCustomerTaxExemptions(64445519);
	}
	
	//	Exception in thread "main" java.rmi.AccessException: 
	//[EJB:010160]Security Violation: User: '<anonymous>' has insufficient permission to access EJB: type=<ejb>, application=cbl696V64, module=ejb/BL9ChargeServicesBeanSec.jar, ejb=amdocsBeans.BL9ChargeServicesHome, method=getUnbilledOcCharges, methodInterface=Remote, signature={amdocs.bl3g.datatypes.BaIdInfo,amdocs.bl3g.datatypes.PayChannelIdInfo,amdocs.bl3g.datatypes.CustomerIdInfo,amdocs.bl3g.datatypes.EntityIdInfo,amdocs.bl3g.datatypes.PaginationInfo}.
	private void testMe10() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx); //amdocsBeans.BL9ChargeServicesBean
		 BL9ChargeServicesHome bl9csh= (BL9ChargeServicesHome) ctx.lookup("amdocsBeans.BL9ChargeServicesBean");
		 BL9ChargeServices bl9cs = bl9csh.create();
		 System.out.println(bl9cs);
		
         long customerId = 78226505;
         long pcn = 45642771;
         long billingArrangementId = 45426931;
         long serviceInstanceId = 600929377;
		 
        amdocs.bl3g.datatypes.PaginationInfo paginationInfo = new amdocs.bl3g.datatypes.PaginationInfo();
    	paginationInfo.setUnAssignedAll();
    	paginationInfo.setPageSize(9999);
    	paginationInfo.setPageNumber(0);
		 
		 amdocs.bl3g.datatypes.CustomerIdInfo custIdInfo = com.telus.billingcare.adjustment.adapter.EnablerBillingDataConverter.
				customerIdToCustomerIdInfo(customerId);
			
		 amdocs.bl3g.datatypes.PayChannelIdInfo pcnIdInfo = com.telus.billingcare.adjustment.adapter.EnablerBillingDataConverter.
				payChannelIdToPayChannelIdInfo(pcn);
			
		 amdocs.bl3g.datatypes.BaIdInfo baIdInfo = com.telus.billingcare.adjustment.adapter.EnablerBillingDataConverter.
				billingArrangementIdToBaIdInfo(billingArrangementId);

		 amdocs.bl3g.datatypes.EntityIdInfo entityIdInfo = new amdocs.bl3g.datatypes.EntityIdInfo();
			
			//Service Instance Id - aka - Service Receiver Id or EntityId 
			if (serviceInstanceId > 0) {
				entityIdInfo.setEntityType((byte)'S'); // Service Receiver Type
				entityIdInfo.setEntityId(serviceInstanceId); //Service Receiver Id
				
				// If we're passing the service instance id, we should set BA Id & PCN to null
				// Based on feedback from AMDOCS - defect 12421. 2006/04/24
				pcnIdInfo.setNullAll();
				baIdInfo.setNullAll();
				
			}
			else {
			    
			    //If we're not passing service instance id, set Entity Id to null
			    entityIdInfo.setNullAll();
			}
		 try {
			bl9cs.getUnbilledOcCharges(baIdInfo, pcnIdInfo, custIdInfo, entityIdInfo, paginationInfo);
		} catch (BillingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			bl9cs.getUnbilledRcCharges(baIdInfo, pcnIdInfo, custIdInfo, entityIdInfo, paginationInfo);
		} catch (BillingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ACMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
	}
	
	
	
	//addressSvc from asf called first before checksvcavailability
	private void testMe9() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		AddressSvcEjbRhome rHome = (AddressSvcEjbRhome) ctx.lookup("ejb/AddressSvcEjb");
		//System.out.println(rHome);
		AddressSvcEjbRemote addressSvcEjbRemote= rHome.create();
		SearchAddressRespDto searchAddressRespDto = null;
		
		//5787 TURNSTONE DRIVE SECHELT BC CANADA V0N3A6
 
		/*ServiceAddress searchAddress = new ServiceAddress();
		searchAddress.setAddressType("1");
		searchAddress.setApartment("6818A");
		searchAddress.setHouseNumberAndSuffix("4448");
		searchAddress.setStreet("FRONT ST SE");
		searchAddress.setCity("CALGARY");
		searchAddress.setProvince(Province.ALBERTA);
		searchAddress.setCountry(ServiceAddress.CANADA);*/
		
		ServiceAddress searchAddress = new ServiceAddress();
		searchAddress.setAddressType("1");
		searchAddress.setHouseNumberAndSuffix("763");
		searchAddress.setStreet("KICHEN CK AVE");
		searchAddress.setCity("GITSEGUKLA");
		searchAddress.setProvince(Province.BRITISH_COLUMBIA);
		searchAddress.setCountry(ServiceAddress.CANADA);
		//this is before fix: searchAddress.setPostalCode("V0J2R0");
		searchAddress.setPostalCode("V0J2J3");
		
		String csrId = "x204097";
		
		/*ServiceAddress searchAddressDetails = new ServiceAddress();
		try{
			searchAddressDetails = addressSvcEjbRemote.getAddressDetails(searchAddress, "x204097");
			System.out.println("Address ID: "+searchAddressDetails.getServiceAddressId());
			System.out.println("Address Type: "+searchAddressDetails.getAddressType());
			System.out.println("Address Apartment: "+searchAddressDetails.getApartment());
			System.out.println("Address House Number: "+searchAddressDetails.getHouseNumberAndSuffix());
			System.out.println("Address Street: "+searchAddressDetails.getStreet());
			System.out.println("Address City: "+searchAddressDetails.getCity());
			System.out.println("Address Province: "+searchAddressDetails.getProvince());
			System.out.println("Address Country: "+searchAddressDetails.getCountry());
			System.out.println("Address Postal Code: "+searchAddressDetails.getPostalCode());
		}
		
		catch (InvalidAddressException iae) {
			System.out.println("Csr Id: " + csrId);
			System.out.println("House Number and Suffix: " + searchAddress.getHouseNumberAndSuffix());
			System.out.println("Street Name: " + searchAddress.getStreet());
			System.out.println("City Name: " + searchAddress.getCity());
			System.out.println("Province: " + searchAddress.getProvince());
			System.out.println("InvalidAddressException: " + iae.getStackTrace());
		}*/
		ServiceAddress searchAddressDetails = new ServiceAddress();
		try{
			searchAddressRespDto = addressSvcEjbRemote.searchServiceAddress(searchAddress, 10, null, csrId );
			System.out.println("-------------------------------");
			searchAddressDetails = searchAddressRespDto.getDetails();
			System.out.println("Address ID: "+searchAddressDetails.getServiceAddressId());
			System.out.println("Address Type: "+searchAddressDetails.getAddressType());
			System.out.println("Address Apartment: "+searchAddressDetails.getApartment());
			System.out.println("Address House Number: "+searchAddressDetails.getHouseNumberAndSuffix());
			System.out.println("Address Street: "+searchAddressDetails.getStreet());
			System.out.println("Address City: "+searchAddressDetails.getCity());
			System.out.println("Address Province: "+searchAddressDetails.getProvince());
			System.out.println("Address Country: "+searchAddressDetails.getCountry());
			System.out.println("Address Postal Code: "+searchAddressDetails.getPostalCode());
			SearchAddressRespType type = searchAddressRespDto.getReturnType();
			
			System.out.println("Address Return Type: "+type.getValue());
		}
		
		catch (InvalidAddressException iae) {
			System.out.println("Csr Id: " + csrId);
			System.out.println("House Number and Suffix: " + searchAddress.getHouseNumberAndSuffix());
			System.out.println("Street Name: " + searchAddress.getStreet());
			System.out.println("City Name: " + searchAddress.getCity());
			System.out.println("Province: " + searchAddress.getProvince());
			System.out.println("InvalidAddressException: " + iae.getStackTrace());
		}
		
		/*ServiceAddress searchAddress = new ServiceAddress();
		//searchAddress.setAddressType("2");
		searchAddress.setApartment("324");
		searchAddress.setHouseNumberAndSuffix("52");
		searchAddress.setStreet("CRANFIELD LINK SE");
		searchAddress.setCity("CALGARY");
		searchAddress.setProvince(Province.ALBERTA);
		searchAddress.setCountry(ServiceAddress.CANADA);
		searchAddress.setPostalCode("T3M1C5");*/
		
		/**AT06 */
		/*ServiceAddress searchAddress = new ServiceAddress();
		searchAddress.setAddressType("1");
		searchAddress.setApartment("432");
		searchAddress.setHouseNumberAndSuffix("4303 1");
		searchAddress.setStreet("ST NE");
		searchAddress.setCity("CALGARY");
		searchAddress.setProvince(Province.ALBERTA);
		searchAddress.setCountry(ServiceAddress.CANADA);
		searchAddress.setPostalCode("T2E7M3");*/
		
		//searchAddress.setLegalLandDescription(null);
//		searchAddress.setPostalCode("");
//		searchAddress.setServiceAddressId("");
		//searchAddress.setVector("");
//		searchAddress.setCoData(new BasicCoData());
		//searchAddress.setPostalCode("V0N3A6");
		
		
	/*
		ServiceAddress searchAddress = new ServiceAddress();
		searchAddress.setAddressType("1");
//		searchAddress.setApartment("");
		searchAddress.setCity("EDMONTON");
		searchAddress.setCountry(ServiceAddress.CANADA);
		searchAddress.setHouseNumberAndSuffix("1106");
		searchAddress.setLegalLandDescription(null);
//		searchAddress.setPostalCode("");
//		searchAddress.setServiceAddressId("");
		searchAddress.setStreet("Secord Promenade");
		searchAddress.setVector("NW");
//		searchAddress.setCoData(new BasicCoData());
		searchAddress.setProvince(Province.ALBERTA);
		//searchAddress.setPostalCode("V3J4T2");
	*/	
		//990 Blue Mountain St Coquitlam BC V3J4T2

		
		/*try {
			ClearancePath[] path = addressSvcEjbRemote.getClearancePaths(searchAddress, "x204097", null)
			System.out.println(sard.getLikeStreets().getRecords().length);
			System.out.println(sard.getLikeStreets().getRecords()[0].getStreet());
		} catch (InvalidAddressException e) {
			// TODO Auto-generated catch block
			System.err.println("Invalid Address Exception");
			e.printStackTrace();
		}*/
		/*ClearancePath[] paths = new ClearancePath[0];
		try {
			paths = addressSvcEjbRemote.getClearancePaths(searchAddress, "x204097", "");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.err.println("Invalid Address Exception");
			e.printStackTrace();
		}
		
		if(paths != null && paths.length != 0){
			for(int i=0; i<paths.length; i++){
				System.out.println("Lenght: "+paths.length);
				System.out.println("S.Path Id: "+paths[i].getServicePathId());
				System.out.println("S.Path Type: "+paths[i].getServiceTypeCode());
				System.out.println("S.Path Switch Type: "+paths[i].getSwitchTypeCode());
				System.out.println("S.Path Status: "+paths[i].getServicePathStatusCode());
				System.out.println("S.Path LeasedLoopFlag: "+paths[i].getLeasedLoopFlag());
			}
		}*/
		
		
		
	}
	 
	//thank God as this helped in understanding the issue!
	private void testMe8() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
//		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
//		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
//		PaymentSvcRemoteHome rHome = (PaymentSvcRemoteHome)ctx.lookup("com.telus.billingcare.payment.service.PaymentSvc");
//		System.out.println(rHome);
//		PaymentSvcRemote paymentSvcRemote = rHome.create();
//		PreAuthorizedPayment pap = paymentSvcRemote.getPapFromMaster("215111268");
//		System.out.println(pap.getCreditCard().getCreditCardNumber());
//		System.out.println(pap.getCreditCard().getCreditCardType());
//		System.out.println(pap.getCreditCard().getCreditCardExpiryDate());
		//System.out.println(pap.getCreditCard().getCreditCardLastDigits());
		PilotCryptoImpl pci = new PilotCryptoImpl(); //do this in spring format!
		pci.setKey1("EbT5a8Fuq");
		pci.setKey2("aYt2gv6R");
		pci.setKey3("9bFp3Gz4k");
		pci.init();
		String decrypted = pci.decrypt("u vOgH59V\"s)8"); //pap.getCreditCard().getCreditCardNumber());
		System.out.println(decrypted);

		
		//Crypto crypto = pci; 
		//EncryptionUtil.setCrypto(pci);
		
		//EncryptionUtil.encrypt("yekdlwdfwed");
		//System.out.println(EncryptionUtil.decrypt(pap.getCreditCard().getCreditCardNumber()));
		
	}
	
	
	
	//com.telus.customerprofilemgt.service.SubscriberProfileOrderingSvc
	private void testSubscriberProfileOrderingSvc() throws NamingException, RemoteException, ObjectNotFoundException,  CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		SubscriberProfileOrderingSvcEjbRHome rHome = (SubscriberProfileOrderingSvcEjbRHome)ctx.lookup("com.telus.customerprofilemgt.service.SubscriberProfileOrderingSvc");
		SubscriberProfileOrderingSvcEjbRemote remote = rHome.create();

		SubscriberProfile sp = new SubscriberProfile();
		
		try {
			remote.createSubscriberProfile(sp, "", 0);
		} catch (IdentityCredentialsRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("test: " + rHome);		
	}
	
	
	//TreatmentSvc
	//uses t3://sedm3216.ent.agt.ab.ca:21221 (prd careful!)
	private void testMe6() throws NamingException, RemoteException, ObjectNotFoundException, InvalidEventTypeException {
		//com.telus.collections.treatment.service.TreatmentService
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);	
		try {
			//the lookup name for below can be checked in the spring config files of DT1 (search this in xml).
		TreatmentSvcEjbRhome rHome = (TreatmentSvcEjbRhome)ctx.lookup("com.telus.collections.treatment.service.TreatmentService");
		System.out.println("test: " + rHome);
		TreatmentSvcEjbRemote treatmentSvcEjbRemote = rHome.create();
		int[] intArr = {235250704, 601116224};
		
			//treatmentSvcEjbRemote.getCollectionStatusByBillingAccountNumbers(intArr);
			//to avoid stream errors, get the latest compiled version
			//in the staging server, and update your local classpath, TG
			//CollectionAccountStatusDto[] casdArr = treatmentSvcEjbRemote.getCollectionStatusByBillingAccountNumbers(intArr);
			
			//System.out.println("ArrLength: " + casdArr.length);
			/*for (CollectionAccountStatusDto casd: casdArr) {
				System.out.println(casd);
			}*/
		}/*catch (TreatmentPreConditionException tpce) {
			System.err.println("error");
			tpce.printStackTrace();
		}*/catch (CreateException ce) {
			ce.printStackTrace();
		}
	}
	
	
	//working: CreditProfileMgtSvc
	//for prd, it uses t3://sedm3216.ent.agt.ab.ca:21023 (careful!!)
	
	private void testMe5() throws NamingException, RemoteException, CreateException, ObjectNotFoundException, InvalidEventTypeException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);	
		//credit profile svc test GTG!
		CreditProfileMgtSvcEjbRhome rHome = (CreditProfileMgtSvcEjbRhome)ctx.lookup("com.telus.credit.service.CreditProfileService");
		System.out.println(rHome);
		CreditProfileMgtSvcEjbRemote creditProfileMgtSvcEjbRemote= rHome.create();
		CreditProfile credProf = creditProfileMgtSvcEjbRemote.getCreditProfile(75916628);
		
		System.out.println(credProf.toString());	
	}
	
	
	//not working no jndi info
	//java.io.InvalidClassException: com.telus.customermgt.domain.Customer; local class incompatible: stream classdesc serialVersionUID = -4811010847033854063, local class serialVersionUID = 4807142253928852743
	private void testMe4() throws NamingException, RemoteException, CreateException, ObjectNotFoundException, InvalidEventTypeException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);	

		//jndi is com.telus.customermgt.service.CustomerMgtService
		//this uses CustomerMgtSvr in env.xml
		CustomerMgtSvcEjbRhome customerMgtSvcEjbRhome = (CustomerMgtSvcEjbRhome) ctx.lookup("com.telus.customermgt.service.CustomerMgtService");
		CustomerMgtSvcEjbRemote customerMgtSvcEjbRemote = customerMgtSvcEjbRhome.create();
				//needs telus-bi-domain-jdk1.6.jar

		//this is working line TG.. 
		CustomerIndividual customer = (CustomerIndividual) customerMgtSvcEjbRemote.getCustomer(20403966); //try either 75916628 or 73372418 - with e500 in DT1
		
		//working? TG..
		//CustomerIndividual customer = (CustomerIndividual) customerMgtSvcEjbRemote.getCustomerByBillingAccountNumber(601794425); //try either 75916628 or 73372418 - with e500 in DT1
		
		//CustomerIndividual customer = (CustomerIndividual) customerMgtSvcEjbRemote.getCustomerEligibilityInfo(20403966);
		System.out.println("the pay channel num is: " + customer.getDefaultPayChannelNumber());
		
		ServiceInstance[] svcInstances = (customer.getServiceInstances() == null ? new ServiceInstance[0]: customer.getServiceInstances());
		
		//System.out.println(customer.getFirstName());
		//System.out.println(customer.getLastName());
		//System.out.println(customer.getFullName());
		//this part id for e500 in the customer summary page. If cs is null, youll recreate the error
		for (int i = 0; i < svcInstances.length; i++) {
			System.out.println(svcInstances[i].getId());
			CustomerSubscription[] custSubscriptionArray = svcInstances[i].getCustomerSubscriptions(); //the
            List subscriptionsList = new ArrayList(Arrays.asList(custSubscriptionArray));  //to recreate issue, custSubscriptionArray is null
            //below is working loop TG!
//			for (int j=0; j < cs.length; j ++ ) {
//				System.out.println("  " + cs[j].getId());
//				
//			}
			for (Iterator iterator = subscriptionsList.iterator(); iterator	.hasNext();) {
				System.out.println("  " + ((CustomerSubscriptionExt)iterator.next()).getId());
			}
			custSubscriptionArray = (CustomerSubscription[]) subscriptionsList
					.toArray(new CustomerSubscription[0]);
			svcInstances[i].setCustomerSubscriptions(custSubscriptionArray);
		}
		
		//i think khaled has provided the wrong service class
		//since the affected module is telus-sc-customerprofilemgt-services 
		//this uses customerProfileMgtSvr in env.xml
		//SubscriberProfileConfigurationSvcEjbRemote.getSubscriberProfilesByServiceInstanceId
//		SubscriberProfileConfigurationSvcEjbRHome subscriberProfileConfigurationSvcEjbRHome = (SubscriberProfileConfigurationSvcEjbRHome) ctx.lookup("com.telus.customerprofilemgt.service.SubscriberProfileConfigurationSvc");
		
		//from env.xml
		//com.telus.customerprofilemgt.service.SubscriberProfileOrderingSvc
		//com.telus.customerprofilemgt.service.SubscriberProfileConfigurationSvc
		//com.telus.customerprofilemgt.service.SubscriberIdentityProfileOrderingSvc
		//com.telus.customerprofilemgt.service.SubscriberIdentityProfileConfigurationSvc
//		SubscriberProfileConfigurationSvcEjbRemote subscriberProfileConfigurationSvcEjbRemote = subscriberProfileConfigurationSvcEjbRHome.create();
//		
//		SubscriberProfile[] subscriberProfileArray = subscriberProfileConfigurationSvcEjbRemote.getSubscriberProfilesByServiceInstanceId(43765851);
//		
//		for (SubscriberProfile sProfile: subscriberProfileArray) {
//			System.out.println("Here start");
//			System.out.println(sProfile.getResourceId());
//			System.out.println(sProfile.getResource().getOrderParams().toString());
//			System.out.println("Here end");
//		}
		
		//cannot find the JNDI name in prescribed server.. so trace the method itself how implementation works.
		
		
	}
	
	
	//for com.telus.collections.treatment.service.PaymentArrangementMgtSvc
	//DT1 arrangement glitch issue
	//this is working GTG!
/*	private void testMe3() throws NamingException, RemoteException, CreateException, ObjectNotFoundException, InvalidEventTypeException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);	
		
		PaymentArrangementMgtSvcEjbRhome paymentArrangementMgtSvcEjbRhome =  (PaymentArrangementMgtSvcEjbRhome) ctx.lookup("com.telus.collections.treatment.service.PaymentArrangementMgtSvc");
		PaymentArrangementMgtSvcEjbRemote paymentArrangementMgtSvcEjbRemote = paymentArrangementMgtSvcEjbRhome.create();
		
		try {
			com.telus.collections.treatment.domain.PaymentArrangement[] pa = paymentArrangementMgtSvcEjbRemote.findPaymentArrangements(601251134, null, null, null, null);
			System.out.println("JEM here in populatePaymentArrangementDetails");
			for (int i = 0; i< pa.length; i++) {
				System.out.println(pa[i].getId());
				System.out.println(pa[i].getStatus());
				System.out.println(pa[i].getAccountRiskIndicator());
				System.out.println(pa[i].getTotalInstallmentCount());
				System.out.println(pa[i].getBillingAccountNumber());
				System.out.println(pa[i].getArrangementAmount());
				System.out.println(pa[i].getArrangementDueDate());
				System.out.println(DateTimeUtil.format( pa[i].getArrangementDueDate(), DateTimeUtil.DATE_ONLY_FORMAT3));
			}
		
		} catch (PaymentArrangementValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}*/
	
	//working GTG!
	/*
	private void testMe2() throws NamingException, RemoteException, CreateException, ObjectNotFoundException, InvalidEventTypeException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		//com.telus.service.UnbilledEventService
		
		UnbilledEventServiceHome m_unbilledEventServiceHome = (UnbilledEventServiceHome) ctx.lookup("com.telus.service.UnbilledEventService");
		UnbilledEventServiceRemote unbilledEventServiceEJB = m_unbilledEventServiceHome.create();
		

		//type checking or pag append
		
		UnbilledEventServiceConfig config = new UnbilledEventServiceConfig();
	    config.setEventType("VOIC"); //needed
	    config.setStartingRowNumber(1);	  //needed
	    config.setEndingRowNumber(100);	  //needed
	    //Error Description : UnbilledEventServiceConfig#verifySortColumn() - Incorrect Sort Column Specified.The sort column for 
	    //wireline event type should be fromthis list : USAGE_EVENT_ID:SERVICE_INSTANCE_ID:EVENT_DURATION_QTY:EVENT_START_DT:EVENT_START_TM:
	    //GROSS_CHARGE_AMT:GST_AMT:HST_AMT:NET_CHARGE_AMT:OFFER_ID:PST_AMT:TAX_PROVINCE_CD:
	    //AUTOMATIC_IDENTIFICATION_CD:MESSAGE_CLASS_CD:ORIG_CARRIER_CD:ORIG_PLACE_NM:SERIAL_NUM:PAYMENT_TYPE_CD:TERM_PLACE_NM:TERM_TEL_NUM
	    config.setSortColumn("SERIAL_NUM DESC"); //needed
	    //config.setUsageEventId(2513015L);
	    config.setServiceInstanceId(6198798L); //needed
	    //config.setEventStartDate("2012-01-01");
	    //config.setEventEndDate("2012-02-01");
	    


	    Date start = new Date();
	    long starttime = start.getTime();
	    UsageEventDto[] ue = unbilledEventServiceEJB.getUnbilledEvents(config);
	    Date end = new Date();
	    long endtime = end.getTime();
	    for (int i = 0; i < ue.length; i++)
	      System.out.println(i + " : " + ue[i] + "\n");
	    System.out.println(endtime - starttime);
		
		
		
	}
	*/
	
	private void testAdjustmentSvc() throws NamingException, RemoteException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		
		 AdjustmentTransactionMgtSvcEjbRHome rHome = (AdjustmentTransactionMgtSvcEjbRHome) ctx.lookup("com.telus.billingcare.adjustment.service.AdjustmentTransactionMgtSvc");
		 AdjustmentTransactionMgtSvcEjbRemote remote = rHome.create();
		 BillingRequest[] requests = remote.getAdjustmentRequestByBillAndServiceInstanceIds(785971399L, 26887619L);
		 
	}

	private void testMe() throws NamingException, RemoteException, CreateException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);
		
		//jem: commented 2 lines below 072516
		//BillingRequestMgtSvcEjbRhome m_billingRequestMgtSvcEjbRhome = (BillingRequestMgtSvcEjbRhome) ctx.lookup("com.telus.billingcare.request.service.BillingRequestMgtSvc");
		//BillingRequestMgtSvcEjbRemote m_BillingRequestMgtSvcEjbRemote= m_billingRequestMgtSvcEjbRhome.create();
		
		
		 AdjustmentTransactionMgtSvcEjbRHome m_adjustmentSvcHome = (AdjustmentTransactionMgtSvcEjbRHome) ctx.lookup("com.telus.billingcare.adjustment.service.AdjustmentTransactionMgtSvc");
		 AdjustmentTransactionMgtSvcEjbRemote m_adjustmentMgtSvc = m_adjustmentSvcHome.create();
		 
		 //parameter for below is billingAccountNo, billedCharges[i].getBilledChargeSequenceNumber(),billedCharges[i].getChargeCode());
		 BigDecimal sumMaxAllowableAmount = null;
		 BigDecimal sumMaxAllowableAmount2 = m_adjustmentMgtSvc.getAllowableAmountInEnablerAR(125060724, 4577973528L, "LD_INTL");
		 BigDecimal tmp = new BigDecimal(0.03);  //there is a difference when you put the value under double quotes or not!
		 sumMaxAllowableAmount = m_adjustmentMgtSvc.getAllowableAmountInEnablerAR(125060724, 4577973528L, "LD_INTL");
		 //sumMaxAllowableAmount = sumMaxAllowableAmount.add(sumMaxAllowableAmount2);
		 System.out.println("here: " + sumMaxAllowableAmount);

		//below is working GTG!
		/*
		TeamMemberMgtServiceHome m_teamMemberMgtServiceHome = (TeamMemberMgtServiceHome) ctx.lookup("com.telus.identity.services.TeamMemberMgtService");
		TeamMemberMgtServiceRemote  m_teamMemberMgtServiceRemote= m_teamMemberMgtServiceHome.create();
		Group[] group = m_teamMemberMgtServiceRemote.findGroupsByTeamMemberID("T000001");
		System.out.println("group.length is " + group.length);		
		for (int i=0; i<group.length; i++) {
			System.out.println("##########################");
			System.out.println(group[i].getGroupNm());
			System.out.println(group[i].getGroupId());
		}
		
		Role[] role = m_teamMemberMgtServiceRemote.findRolesByTeamMemberID("T000001");
		System.out.println("role.length is " + group.length);		
		for (int i=0; i<role.length; i++) {
			System.out.println("##########################");
			System.out.println(role[i].getRoleNm());
			System.out.println(role[i].getRoleId());
		}
		*/
		
		//this is for the LDAP issue - working query, GTG..
		/*
		BillingRequestMgtSvcEjbRhome m_billingRequestMgtSvcEjbRhome = (BillingRequestMgtSvcEjbRhome) ctx.lookup("com.telus.billingcare.request.service.BillingRequestMgtSvc");
		BillingRequestMgtSvcEjbRemote m_BillingRequestMgtSvcEjbRemote= m_billingRequestMgtSvcEjbRhome.create();
		BillingRequestSearchCriteriaDto arg0 = new BillingRequestSearchCriteriaDto();
		arg0.setBillingAccountNumber(600537492);
		boolean arg1 = true;		
		m_BillingRequestMgtSvcEjbRemote.findBillingRequests(arg0, arg1);
		*/
		
		
		//THis is for QC11864: telus_D1-enterprise-9_1telus-sc-customerprofilemgt-services-jdk16_jarSubscriberProfileConfigurationSvcEjb_EO
//		//the telus code has little or no logging, which makes debugging like hell
//		SubscriberProfileOrderingSvcEjbRHome m_subscriberProfileOrderingSvcEjbRHome = (SubscriberProfileOrderingSvcEjbRHome) ctx.lookup("com.telus.customerprofilemgt.service.SubscriberProfileOrderingSvc");
//		SubscriberProfileOrderingSvcEjbRemote m_subscriberProfileOrderingSvcEjbRemote= m_subscriberProfileOrderingSvcEjbRHome.create();
//		SubscriberProfileDto subscriberProfileDTO = new SubscriberProfileDto();
//		subscriberProfileDTO.setCurrentCustomerId(currentCustomerId);
//		subscriberProfileDTO.setEventDate(eventDate);
//		IdentityParameter identityParameter = new IdentityParameter();
//		identityParameter.set
//		subscriberProfileDTO.setIdentityParameter(identityParameter);
//		subscriberProfileDTO.setMasterRequestRef(masterRequestRef);
//		subscriberProfileDTO.setMasterRequestSourceId(masterRequestSourceId);
//		subscriberProfileDTO.setNewCustomerId(newCustomerId);
//		subscriberProfileDTO.setServiceResourceValueId(serviceResourceValueId);
//		subscriberProfileDTO.setServiceTypeCode(serviceTypeCode);
//		m_subscriberProfileOrderingSvcEjbRemote.resumeSubscriberProfiles(arg0);
		
		
		//commented web code: for editing
//        try {
//            Object home = ctx.lookup(jndiName);
//            return (CallingCardConfigSvcEjbRhome) PortableRemoteObject.narrow(
//                    home, CallingCardConfigSvcEjbRhome.class);
//
//        } catch (NamingException ne) {
//            System.out.println("The client was unable to lookup the EJBHome. "
//                    + "Please make sure "
//                    + "that you have deployed the ejb with the JNDI name "
//                    + jndiName + " on the WebLogic server at " + url);
//            throw ne;
//        }
		
			//Home  -create (or factory methods)
			//remote - business methods in the EJB
		

		
		//m_teamMemberMgtServiceRemote.updateRoleAssociations(aTeamMemberID, roles);
		
		//in this point you are getting the same exception when running your teammember-services ear locally - fixed by adding frameworks core jar to export
//		Exception in thread "main" java.rmi.RemoteException: EJB Exception: ; nested exception is: 
//			java.lang.ExceptionInInitializerError
//			at weblogic.rjvm.ResponseImpl.unmarshalReturn(ResponseImpl.java:234)
//			...
//		Caused by: java.lang.ExceptionInInitializerError
//			at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
//			at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:39)
//			...
//		Caused by: org.apache.commons.logging.LogConfigurationException: User-specified log class 'com.telus.framework.logging.Log4JLogger' cannot be found or is not useable.
//			at org.apache.commons.logging.impl.LogFactoryImpl.discoverLogImplementation(LogFactoryImpl.java:874)
//			at org.apache.commons.logging.impl.LogFactoryImpl.newInstance(LogFactoryImpl.java:604)		
//		

		
		
		
		
		
		
		//m_teamMemberMgtServiceRemote.findTeamMemberByName("e", "ee");
		
		System.out.println("Connecting to:"
				+ LocalEJBConnectionTest.getLocalDeploymentContext().get(
						Context.PROVIDER_URL));
		System.out.println("Connection to local server successful");
	}
	
	private void testExchangeInfoService() throws NamingException, RemoteException, ObjectNotFoundException, InvalidEventTypeException {
		ctx = new InitialContext(LocalEJBConnectionTest.getLocalDeploymentContext());
		LocalEJBConnectionTest.showJndiProp((InitialContext) ctx);	
		try {
			//the lookup name for below can be checked in the spring config files of DT1 (search this in xml).
			ExchangeInfoSvcEjbRhome rHome = (ExchangeInfoSvcEjbRhome)ctx.lookup("com.telus.referenceods.exchangeinfo.service.ExchangeInfoService");
			System.out.println("test: " + rHome);
			ExchangeInfoSvcEjbRemote exchangeInfoSvcEjbRemote = rHome.create();
			
			String[] phones = {"7809205746", "5873721019", "100000568951", "1003156769", "7804633013", "20031399815"};
			
			/*String[] phones = {
					"20003786670",
					"20009162309",
					"6042707385",
					"6042707385",
					"100850040",
					"20014637994",
					"7788737955",
					"20014782718",
					"20008766660"
			};*/
			
			String regex = "[^0-9]";
			for(String phone: phones){
				phone = phone.replaceAll(regex, "");
				
				if (phone != null && phone.length() >= 6){
					String npaNxx = phone.substring(0, 6);
					System.out.println("-------------------------------------------");
					System.out.println("npaNxx: "+npaNxx);
					try{
						ExchangeForborneStatus forborneStatus = exchangeInfoSvcEjbRemote.getForborneStatusByNpaNxx(npaNxx, ServiceClassRateConstant.RESIDENTIAL, new Date());
						System.out.println("Forborne Status Type Code: "+forborneStatus.getStatusTypeCode());
					}
					catch (ObjectNotFoundException onfe) {
						System.out.println("ObjectNotFoundException!!!");
					}
				
				}
			}
			
			//to avoid stream errors, get the latest compiled version
			//in the staging server, and update your local classpath, TG
			
			/*CollectionAccountStatusDto[] casdArr = exchangeInfoSvcEjbRemote.getCollectionStatusByBillingAccountNumbers(intArr);

			System.out.println("ArrLength: " + casdArr.length);
			for (CollectionAccountStatusDto casd: casdArr) {
				System.out.println(casd);
			}*/
		}catch (Exception e) {
			System.err.println("error");
			e.printStackTrace();
		}
	}

	// public void testCreateAccountLevelAdjustment() throws RemoteException
	// {
	// System.out.println("start of test Creates1");
	// BillingRequest billingRequest =
	// AdjustmentTestCaseUtils.createBillingRequest();
	// billingRequest.setBillingTransactions(AdjustmentTestCaseUtils.createBillingAccountAdjustments(1));
	// billingRequest.setBillingAccountNumber(6551);
	// billingRequest.setCreateDate(new Date());
	//
	//
	// try{
	// m_adjustmentMgtSvc.applyCredit(billingRequest);
	//
	//
	// System.out.println("applyCredit was a success");
	// }
	// catch(Exception ex){
	// System.out.println("exception -> " + ex.getMessage());
	// ex.printStackTrace();
	// }
	// }

}