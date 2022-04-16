package com.telus.paymentgateway;

import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.telus.billingcare.payment.service.ejb.PaymentSvcRemote;
import com.telus.billingcare.payment.service.ejb.PaymentSvcRemoteHome;

public class CareEjbClient {

	private Context ctx;
	
	public CareEjbClient(String providerUrl) throws NamingException {
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
		CareEjbClient client = new CareEjbClient("t3://ln98438:41357,ln98439:41357");
		CareEjbClient.showJndiProp(client.getContext());
		client.testTransferPayment();
	}
    
    public void testTransferPayment() throws NamingException, RemoteException, CreateException {
    	PaymentSvcRemoteHome rHome = (PaymentSvcRemoteHome) ctx.lookup("com.telus.billingcare.payment.service.PaymentSvc");
		PaymentSvcRemote paymentSvcRemote = rHome.create();
		System.out.println(paymentSvcRemote == null);
    }
	
}