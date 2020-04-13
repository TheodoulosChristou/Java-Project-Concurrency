import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

import static java.lang.System.out;

public class Server {

    /**
     * Runs the server. When a client connects, the server spawns a new thread to do
     * the servicing.
     */
    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(4242)) {
            ExecutorService pool = Executors.newFixedThreadPool(1000);
            while (true) {
                pool.execute(new Talk(listener.accept()));
            }
        }
    }


    /* this is an ArrayList that stores all the Brokers from the Client Class
       with all the details.
     */
    public static ArrayList<Client> clientArrayList = new ArrayList<>();


    /*this is an ArrayList that stores all the accountNumbers of all
      the clients.
     */
    public static ArrayList<Integer> accountNumbers = new ArrayList<Integer>();

    /* this is the value of rate that is constant to 10.0 */
    public static double rate = 10.0;


    private static class Talk implements Runnable {
        private Socket socket;

        Talk(Socket socket) {
            this.socket = socket;
        }

        /**
         * This method has all the commands that the User wants
         * for the server and tries to connect and also to catch Errors
         * if has Errors or if the user puts wrong command.
         */
        public void run() {
            out.println("Connected: " + socket);
            try {
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                /**
                 * This loop searches if the user puts a command and then creates
                 * a string that takes the Scanner in and then does the cases
                 * for all possible commands and if the command is true then it calls the
                 * right method to print the output.
                 */
                while (in.hasNextLine()) {
                    String command = in.next();
                    switch (command) {
                        case "open":
                            openCommand(in, out);
                            break;
                        case "state":
                            stateCommand(out);
                            break;
                        case "transfer":
                            transferCommand(in, out);
                            break;
                        case "rate":
                            rateCommand(in, out);
                            break;
                        case "convert":
                            convertCommand(in, out);
                            break;
                        default:
                            out.println("Wrong Command Please put the right command");
                            break;
                    }
                }
            } catch (Exception e) {
                out.println("Error:" + socket);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {

                }
                out.println("Closed: " + socket);
            }
        }


        /**
         * This method opens a new account with both Arian value
         * and Press set to 0.0 and tries to catch if it has errors
         * for the account or if the user forget to put the account Number,
         * and does synchronization for protection of the race conditions.
         * @param in
         * @param out
         * @throws InterruptedException
         */
        private synchronized void openCommand(Scanner in, PrintWriter out) throws InterruptedException {
            if (in.hasNextInt()) {
                /**
                 * checks if the user puts the account Number
                 * if it is then create an integer value that gets the
                 * scanner for the account number.
                 */
                int accountNo = in.nextInt();
                if (accountNumbers.contains(accountNo)) {
                    /**
                     * checks if the account Number is already in the
                     * list of all account Numbers if it is then
                     * shows a message for the user for the account Number.
                     */
                    out.println("The account has already used");
                } else {
                    /**
                     * else creates an object for broker/client add
                     * the account Number, add the object broker to
                     * two arrayLists one for the clients/brokers
                     * and one for account Numbers and print the message for
                     * the broker.
                     */
                    Client client = new Client(accountNo);
                    accountNumbers.add(accountNo);
                    clientArrayList.add(client);
                    out.println("Opened account " + accountNo);
                }
            } else {
                /**
                 * if it's not then show that message to the broker.
                 */
                out.println("Put the number");
            }
        }


        /**
         * This method prints the current state of all accounts
         * and the current conversion rate.
         * @param out
         * @throws InterruptedException
         */
        private void stateCommand(PrintWriter out) throws InterruptedException {
            for (int i = 0; i < clientArrayList.size(); i++) {
                //out.println(array.get(i).toString());
                out.println(clientArrayList.get(i).accountNo + ": " + "Arian " + clientArrayList.get(i).getArian()
                            + ", " + "Press " + clientArrayList.get(i).getPress());
            }
            out.println("Rate " + rate);
        }


        /**
         * This method moves a Arian and p Pres from one account to another
         * checks for errors in values of accounts,arian,pres.
         * @param in
         * @param out
         * @throws InterruptedException
         */
        private void transferCommand(Scanner in, PrintWriter out) throws InterruptedException {
            if (in.hasNext() && in.hasNextDouble() && in.hasNextInt()) {
                /**
                 * checks if the broker puts the values of accountNumber,
                 * arian and pres and if it is then reads the two accounts
                 * the tuples for the arian and pres and puts the scanner in the
                 * values.
                 */
                int account1 = in.nextInt();
                int account2 = in.nextInt();
                String tuple = in.next();
                tuple = tuple.substring(1, tuple.length() - 1);
                Scanner tTransfer = new Scanner(tuple).useDelimiter(",");
                double ar = tTransfer.nextDouble();
                double pres = tTransfer.nextDouble();
                if (accountNumbers.contains(account1) && accountNumbers.contains(account2)) {
                    /**
                     * checks if the two account Numbers are in the list of the
                     * AccountNumbers if it is, then goes throw the loop of the clientList
                     */
                    for (int i = 0; i < clientArrayList.size(); i++) {
                        /**
                         * goes throw the loop
                         */
                        if (clientArrayList.get(i).getAccountNo() == account1) {
                            /**
                             * checks if the account Number is in the ArrayList if
                             * it is,then creates the objects for the clients.
                             */
                            Client c1 = clientArrayList.get(i);
                            Client c2 = clientArrayList.get(i);
                            if (c1.getAccountNo() > c2.getAccountNo()) {
                                /**
                                 * checks if the accountNumber for the client1
                                 * is greater then the accountNumber of the client 2
                                 * if it is, then do the synchronizations blocks for the
                                 * objects of the brokers in the arrayList.
                                 */
                                synchronized (clientArrayList.get(i)) {
                                    /**
                                     * Firstly,does the synchronization block for the first
                                     * client then it does the synchronization block for the second so the
                                     * purpose of that is for the first client to do all the actions
                                     * and when it will exits the block then the second client does
                                     * the actions after the first client,then does the calculations for
                                     * the arian and pres.
                                     */
                                    synchronized (clientArrayList.get(i)) {
                                        clientArrayList.get(i).setArian(clientArrayList.get(i).getArian() - ar);
                                        clientArrayList.get(i).setPress(clientArrayList.get(i).getPress() - pres);
                                    }
                                    if (clientArrayList.get(i).accountNo == account2) {
                                        /**
                                         * checks if the second accountNumber is in the arrayList
                                         * if it is then does the calculations including the synchronization
                                         * block.
                                         */
                                        clientArrayList.get(i).setArian(clientArrayList.get(i).getArian() + ar);
                                        clientArrayList.get(i).setPress(clientArrayList.get(i).getPress() + pres);
                                    }
                                }
                            }
                        }
                    }
                    /**
                     * and show the message after the calculations.
                     */
                    out.println("Transferred");
                } else {
                    /**
                     * else show the message for the accounts.
                     */
                    out.println("One of the accounts doesn't exist ");
                }
            } else {
                /**
                 * else show the message for the all the values.
                 */
                out.println("Put again the values");
            }
        }


        /**
         * This method sets the conversion rate to the given rate.
         * The rate in interpreted as how many units of Pres equal one unit
         * Also checks for errors and does the synchronization for the
         * protection of the race conditions.
         * @param in
         * @param out
         * @throws InterruptedException
         */
        private synchronized void rateCommand(Scanner in, PrintWriter out) throws InterruptedException {
            if (in.hasNextInt()) {
                /**
                 * checks if the broker puts the the value for rate
                 * if it is then takes the value rate and puts the Scanner.
                 *
                 */
                rate = in.nextDouble();
                if (rate == 0.0) {
                    /**
                     * checks if the rate is equal to zero
                     * if it is then show the broker that his/her
                     * can't set the value to zero.
                     */
                    out.println("You can't set the rate to 0.0");
                } else {
                    /**
                     * else shows the message
                     * that the rate changed.
                     */
                    out.println("Rate Changed");
                }
            } else {
                /**
                 * else shows the message for the broker
                 * that the value of the rate isn't correct.
                 */
                out.println("Please put the rate value");
            }
        }


        /**
         * This method converts Arian to Pres and vice versa within an account,
         * where rate is the current conversion rate. Checks for the errors.
         * @param in
         * @param out
         * @throws InterruptedException
         */
        public void convertCommand(Scanner in, PrintWriter out) throws InterruptedException {
            if (in.hasNextInt() && in.hasNext() && in.hasNextDouble()) {
                /**
                 * checks if the broker puts the values of accountNumber,
                 * arian and pres and if it is then reads the account
                 * the tuples for the arian and pres and puts the scanner in the
                 * values.
                 */
                int account = in.nextInt();
                String tuplesConvert = in.next();
                tuplesConvert = tuplesConvert.substring(1, tuplesConvert.length() - 1);
                Scanner tConvert = new Scanner(tuplesConvert).useDelimiter(",");
                double arian = tConvert.nextDouble();
                double press = tConvert.nextDouble();
                if (accountNumbers.contains(account)) {
                    /**
                     * checks if the account is in the list of
                     * all the account Numbers.
                     */
                    if (arian == 0.0 || press == 0.0) {
                        /**
                         * checks if the value of arian or press are
                         * equal to zero then goes throw the arrayList of the
                         * client Array List.
                         */
                        for (int i = 0; i < clientArrayList.size(); i++) {
                            if (clientArrayList.get(i).accountNo == account) {
                                /**
                                 * checks if the accountNumber for the client
                                 * is is in the clientArrayList then create an object
                                 * and gets the accountNumbers for the array.
                                 */
                                Client newClient = clientArrayList.get(i);
                                synchronized (newClient) {
                                    /**
                                     * So,does the synchronization block for the
                                     * client the purpose of that is for the first client to do all the actions
                                     * and then the second client does the actions after the first client,then does
                                     * the calculations for the arian and pres.
                                     */
                                    clientArrayList.get(i).setArian(clientArrayList.get(i).getArian()
                                                                    - arian + press / rate);
                                    clientArrayList.get(i).setPress(clientArrayList.get(i).getPress()
                                                                    - press + arian * rate);
                                }
                            }
                        }
                        out.println("Converted");
                    } else {
                        /**
                         * else shows a message that the value of arian and press
                         * must be 0.
                         */
                        out.println("one of the arian or press must be 0.0");
                    }
                } else {
                    /**
                     * else shows that the account Number does'nt belong
                     * to the list.
                     */
                    out.println("The account doesnt in a list of the client");
                }
            } else {
                /**
                 * else shows that the the broker must put the account,
                 * arian and pres.
                 */
                out.println("Put the account and two values of arian and press");
            }
        }


    }
}