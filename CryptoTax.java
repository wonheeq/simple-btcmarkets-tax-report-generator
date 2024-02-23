import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

import Controller.TradeController;

public class CryptoTax
{
    private static Scanner sc = new Scanner(System.in);
    private static TradeController tc = new TradeController();

    public static void main(String[] args)
    {
        
        menu();
    }


    private static void menu()
    {
        String limit = "ALL";
        boolean exit = false;
        int input = 0;

        do
        {
            print("CryptoTax");
            print("    > 1. Import Files\n"
                + "    > 2. Check Data (debug)\n"
                + "    > 3. View Report\n"
                + "    > 4. Limit Reporting Range\n"
                + "    > 0. Exit\n"
            );

            input = sc.nextInt();
            switch (input)
            {
                case 1:
                {
                    importFile();
                    break;
                }
                case 2:
                {
                    tc.display();
                    break;
                }
                case 3:
                {
                    tc.sort();
                    tc.process();
                    tc.report();
                    break;
                }
                case 4:
                {
                    boolean exitFour = false;

                    do{
                        print("Current range limit: " + limit);
                        print("Please enter financial year to limit reporting range or ALL: ");
                        print("If you want reporting for the financial year 2021-2022, enter 2022");
                        limit = sc.next();

                        if (limit.equals("ALL")) {
                            tc.limit(0);
                            exitFour = true;
                        }
                        else
                        {
                            try {
                                int num = Integer.parseInt(limit);
                                tc.limit(num);
                                exitFour = true;
                            }
                            catch (NumberFormatException | InputMismatchException e ) {
                                print("Invalid input entered - enter a year or ALL");
                            }
                        }
                    } while (!exitFour);
                    break;
                }
                case 0:
                {
                    exit = true;
                    break;
                }
                default:
                {
                    print("Invalid input.");
                    break;
                }
            }

        } while (!exit);
    }

    private static void print(String s)
    {
        System.out.println(s);
    }

    private static void importFile()
    {
        print("Enter File Name:");

        String fileName = sc.next();
        File file = null;
        FileReader reader = null;
        BufferedReader br = null;
        try
        {
            file = new File(fileName);
            reader = new FileReader(file);
            br = new BufferedReader(reader);

            String line, date, type, asset;
            double volume, price, fee, value;

            br.readLine();

            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(",");
                
                date = parts[1];
                type = parts[2];
                asset = parts[3];
                volume = Double.parseDouble(parts[5]);
                price = Double.parseDouble(parts[6]);
                fee = Double.parseDouble(parts[7]);
                value = Double.parseDouble(parts[8]);

                tc.addTrade(date, type, asset, volume, price, fee, value);
            }
        }
        catch (IOException e)
        {
            print("FAILED");
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException e) {}
        }

        tc.sort();
    }
}