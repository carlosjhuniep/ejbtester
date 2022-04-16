package com.telus.paymentgateway;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.telus.framework.util.OriginatingUserType;
import com.telus.paymentgateway.common.dto.CCPaymentRequestDto;
import com.telus.paymentgateway.common.dto.CreditCardInfoDto;
import com.telus.paymentgateway.exception.PaymentReversalException;
import com.telus.paymentgateway.reversal.service.dto.PaymentReversalRequestDto;
import com.telus.paymentgateway.reversal.service.dto.PaymentReversalResponseDto;
import com.telus.paymentgateway.reversal.service.dto.PaymentReversalResultDto;
import com.telus.paymentgateway.service.ejb.PaymentGatewaySvcEjbRemote;
import com.telus.paymentgateway.service.ejb.PaymentGatewaySvcEjbRhome;

public class PgwEjbClient {

	private Context ctx;
	
	public PgwEjbClient(String providerUrl) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>(2);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		env.put(Context.PROVIDER_URL, providerUrl);
		ctx = new InitialContext(env);
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
            }  
            System.out.println("---------------------------------------------");    
        } catch (NamingException namingException) {  
            namingException.printStackTrace();  
        }  
    }
    
    public InitialContext getContext() {
    	return (InitialContext) this.ctx;
    }
    
    public static void main(String[] args) throws NamingException, RemoteException, CreateException {
		PgwEjbClient client = new PgwEjbClient("t3://ln98438:46221,ln98439:46221");
		PgwEjbClient.showJndiProp(client.getContext());
		client.testReversePayment();
	}
    
    public void testReversePayment() {
    	PaymentGatewaySvcEjbRhome rHome;
		try {
			rHome = (PaymentGatewaySvcEjbRhome) ctx.lookup("com.telus.paymentgateway.service.PaymentGatewaySvc");
			PaymentGatewaySvcEjbRemote remote = rHome.create();
			System.out.println(remote == null);
			String tpid = "911034550000001102005-11-28"; 
			// "932117060000002202014-02-07"
			
			PaymentReversalResponseDto pRevResDto = remote.getReversalPayments(tpid);

			/*CreditCardInfoDto creditCardInfo = new CreditCardInfoDto("4444111111111111", "VI", 2022, 12);
			CCPaymentRequestDto ccpmt = new CCPaymentRequestDto(1001, "t000001", "EN", "602991842", 1001, new BigDecimal("1.00"), false, "", creditCardInfo);
			OriginatingUserType out = new OriginatingUserType();
			out.setCustId("");
			remote.processPayment(ccpmt, out);*/
			
	        PaymentReversalRequestDto dto = new PaymentReversalRequestDto();
			dto.setPaymentId(tpid);
			dto.setCallerSystemId(new Integer(1003));
			dto.setUserId("t00001");                       
			dto.setRemark("Any text: Duplicate File");                 
			dto.setAdjustmentReasonCode("RVDUP"); // CBCC, CBOTCC, RVMISC, RVDUP, CBBNK, RVEDI, RVEXCH, RVINTC
			PaymentReversalResultDto[] response = remote.reversePayment(dto);
			for (int i = 0; i < response.length; i++) {
				PaymentReversalResultDto prrd = response[i];
				System.out.println(prrd);
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CreateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PaymentReversalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
}