package com.luv2code.aopdemo.aspect;

import com.luv2code.aopdemo.Account;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Order(2)
public class MyDemoLoginAspect {

    @Around("execution(* com.luv2code.aopdemo.service.AccountDAO.getFortune(..))")
    public Object aroundGetFortune(
            ProceedingJoinPoint theProceedingJoinPoint) throws Throwable{

        //print out which method we are advising on
        String method = theProceedingJoinPoint.getSignature().toShortString();
        System.out.println("\n======>> Executing @Around on method: "+method);

        //get begin timeStamp
        long begin = System.currentTimeMillis();

        //execute method
        Object result = null;
        try {
            result = theProceedingJoinPoint.proceed();
        }catch (Exception exc){

            //log the exception
            System.out.println(exc.getMessage());

            //give user a custom message
            //result =  "major accident";

            //rethrow exception
            throw exc;
        }

        //get end timeStamp
        long end = System.currentTimeMillis();

        //compute duration and display it
        long   duration = end - begin;
        System.out.println("\n====>> duration "+duration/1000.0 + "seconds");

        return null;
    }

    @After("execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))")
    public void afterFinallyFindAccountsAdvice(JoinPoint theJoinPoint){

        //print out which method we are advising on
        String method = theJoinPoint.getSignature().toShortString();
        System.out.println("\n======>> Executing @After (finally) on method: "+method);
    }

    @AfterThrowing(
            pointcut = "execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))",
            throwing = "theExc"
    )
    public void afterThrowingFindAccountsAdvice(
            JoinPoint theJoinPoint, Throwable theExc
    ){

        //print out which method we are advising on
        String method = theJoinPoint.getSignature().toShortString();
        System.out.println("\n======>> Executing @AfterThrowing on method: "+method);

        // log the exception
        System.out.println("\n======>> the exception is: "+theExc);

    }

    //add a new advice for @AfteReturning on the findAccountsAdvice()
    @AfterReturning(
            pointcut = "execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))",
            returning = "result"
    )
    public void afterReturningFindAccountsAdvice(JoinPoint theJoinPoint, List<Account> result){

        //print out which method we are advising on
        String method = theJoinPoint.getSignature().toShortString();
        System.out.println("\n======>> Executing @AfterReturning on method: "+method);

        //print out the results of the method call
        System.out.println("\n======>> result is: "+result);

        //let's post-process the data

        //convert the account names to uppercase
        convertAccountNamesToUpperCase(result);
        System.out.println("\n======>> result is: "+result);

    }

    private void convertAccountNamesToUpperCase(List<Account> result){

        //loop through accounts

        for (Account tempAccount:result){

            //get uppercase version of name
            String theUpperName = tempAccount.getName().toUpperCase();

            //update the name on the account
            tempAccount.setName(theUpperName);
        }
    }

    @Before("com.luv2code.aopdemo.aspect.LuvAopExpressions.forDaoPackageNoGetterSetter()")
    public void beforeAddAccountAdvice(JoinPoint theJointPoint){
        System.out.println("\n=====>> Executing @Before advice on method");

        //display the method signature
        MethodSignature methodSignature = (MethodSignature) theJointPoint.getSignature();
        System.out.println("Method: "+methodSignature);

        //display the method arguments

        //get args
        Object[] args = theJointPoint.getArgs();

        //loop through args
        for(Object tempArg:args){
            System.out.println(tempArg);

            if (tempArg instanceof Account){
                //downcast and print Account specific stuff
                Account theAccount = (Account) tempArg;
                System.out.println("account name: "+ theAccount.getName());
                System.out.println("account level: "+ theAccount.getLevel());

            }
        }
    }

}
