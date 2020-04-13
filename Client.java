/**
 * This class creates the broker that has the number of the
 * Arian Value, Press Value and his/her AccountNo and for that it
 * has setters getters and the constructor which the arian and press
 * is always in 0.0.
 */
public class Client {

    /* this is the value of arian */
    public double arian;

    /* this is the value of press */
    public double pres;

    /* this is the value of Account No */
    public int accountNo;


    /**
     * This method is the constructor which
     * has the account Number as a parameter
     * and arian and press set to 0.0.
     * @param accountNo
     */
    public Client(int accountNo){
        this.arian = 0.0;
        this.pres = 0.0;
        this.accountNo = accountNo;
    }


    /**
     * This method sets the value of arian.
     * @param arian
     */
    public  void setArian(double arian){
        this.arian = arian;
    }


    /**
     * This method sets the value of press.
     * @param pres
     */
    public  void setPress(double pres){
        this.pres = pres;
    }


    /**
     * This method sets the value of Account Number.
     * @param accountNo
     */
    public  void setAccountNo(int accountNo){
        this.accountNo = accountNo;
    }


    /**
     * This method gets the value of arian.
     * @return arian
     */
    public  double getArian(){
        return arian;
    }


    /**
     * This method gets the value of press.
     * @return pres
     */
    public  double getPress(){
        return pres;
    }


    /**
     * This method gets the value
     * of the Account Number.
     * @return accountNo
     */
    public  double getAccountNo(){
        return accountNo;
    }
}
