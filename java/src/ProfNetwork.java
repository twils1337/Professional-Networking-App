/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 * Name: Tyler Wilson   ,    Billy Xiao
 * CS166 -  Spring 2015
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Calendar;
import java.sql.PreparedStatement;



/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
       // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = false;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            System.out.println("Login");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Go to Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Go to Inbox");
                System.out.println("4. Manage Request");
                System.out.println("5. Search for someone.");  //Done, will fix print for spec though
                System.out.println("6. Change password.");   //Done
                System.out.println("7. Send Connection Request to someone");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql,authorisedUser); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: Inbox(esql,authorisedUser); break;
                   case 4: manage_Request(esql, authorisedUser); break;
                   case 5: Search(esql,authorisedUser); break;
                   case 6: ChangePW(esql, authorisedUser); break;
                   case 7:{
                      System.out.println("Who would you like to add?");
                      String ans = in.readLine();
                        ans = get_userID(esql,ans.trim());
                        send_Request(esql,authorisedUser,ans);
                        ans = get_full_name(esql,ans);
                        send_Request(esql,authorisedUser,ans);
                        break;
                   }
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	       if (userNum > 0)
		         return login;
         return null;
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end
   public static void ChangePW(ProfNetwork esql, String user)
   {
       try{
         System.out.print("\tEnter a new password: ");
         String new_pw = in.readLine();
         String update_pw = String.format("UPDATE USR SET password='%s' WHERE userId = '%s'",new_pw, user);
         esql.executeUpdate(update_pw);
         System.out.println("\tPassword has been updated.");
        }
        catch(Exception e)
        {
           System.err.println (e.getMessage ());
        }
   }//end
   public static void FriendList(ProfNetwork esql, String authorisedUser)
   {
      try
      {
          boolean cont = true;
          boolean init = true;
          //Used to count how many friends initially
          String view_friend = String.format("SELECT name FROM USR WHERE userID IN (SELECT connectionID FROM CONNECTION_USR WHERE userID = '%s' AND status = 'Accept')", authorisedUser);
          String view_friend_2 = String.format("SELECT name FROM USR WHERE userID IN (SELECT userID FROM CONNECTION_USR WHERE connectionID = '%s' AND status = 'Accept')", authorisedUser);

          System.out.println();
          String at = "Nobody"; //current user being viewed

          //Print out Friends List if any and keeps track of how many friends
          int q1 = esql.executeQueryAndPrintResult(view_friend);
          int q2 = esql.executeQueryAndPrintResult(view_friend_2);
          int total_friends = q1+q2;
          int level = -1;
          Map viewing = new HashMap();//used to keep track of level depth-ness
          System.out.println("\tWhat do you want to do?");
          System.out.println("1. View a friend's profile");
          System.out.println("9. Return to main menu.");
          switch (readChoice())
          {
             case 1:
             {
                  while(init)
                  {
                    System.out.println("Whose profile do you want to go to?");
                    String look_at = in.readLine();
                    look_at = look_at.trim();
                    String is_exist = String.format("SELECT U.userId FROM USR U WHERE U.name = '%s'", look_at);
                    int q = esql.executeQuery (is_exist);
                    if (q != 0){ //if user entry exists
                      if (viewing.containsKey(look_at)){ //if profile was already accessed, go back to that level
                        level = (int)viewing.get(look_at);
                      }
                      else{ //new profile access, add to map
                        level++;
                        viewing.put(look_at,level);
                      }
                      String c_usr = get_full_name(esql,authorisedUser);
                      print_prof(esql,c_usr, look_at);
                      init = false;
                      at = look_at; //set to currently viewing person
                    }
                    else{
                      System.out.println("User Does Not Exists\n");
                    }
                  }//end while
                  break;
               }
               case 9:  cont = false; break;
               default: cont = false; break;
            }//end switch

          //Loop after profile is printed
          while (cont){
            System.out.println("\n\tWhat do you want to do?");
            System.out.println("1. View another friend's profile");
            System.out.println("2. View current person's friends.");
            System.out.println("3. Send message to person currently being viewed.");
            if (level <= 3 || (total_friends <= 5)) {//no friends, permit up to 5
              System.out.println("4. Send Connection Request to person currently being viewed.");
            }
            System.out.println("9. Return to main menu.");

            switch (readChoice())
            {
               case 1:
               {
                  init = true;
                  while(init)
                  {
                    System.out.println("Whose profile do you want to go to?\n");
                    String look_at = in.readLine();
                    look_at = look_at.trim();
                    String is_exist = String.format("SELECT U.userId FROM USR U WHERE U.name = '%s'", look_at);
                    int q = esql.executeQuery (is_exist);
                    if (q != 0){ //if user entry exists
                      if (viewing.containsKey(look_at)){ //if profile was already accessed, go back to that level
                        level = (int)viewing.get(look_at);
                      }
                      else{ //new profile access, add to map
                        level++;
                        viewing.put(look_at,level);
                      }
                      String c_usr = get_full_name(esql,authorisedUser);
                      print_prof(esql, c_usr, look_at);
                      init = false;
                      at = look_at; //set to currently viewing person
                    }
                    else{
                      System.out.println("User Does Not Exists\n");
                    }
                  }//end while
                  break;
                }
               case 2:{
                 view_friends(esql, at);
                 break;
               }
               case 3:
               {
                  String c_usr = get_userID(esql,at);
                  sendMsg(esql, authorisedUser, c_usr);
                  break;
               }
               case 4:{
                  String c_usr = get_userID(esql,at);
                  send_Request(esql,authorisedUser,c_usr);
                  break;
                }
               case 9:  cont = false; break;
               default: cont = false; break;
            }//end switch
          }//end while
      }
      catch(Exception e)
      {
          System.err.println (e.getMessage ());
      }
   }

   public static void manage_Request(ProfNetwork esql, String authorisedUser){
      try{
          boolean cont = true;
          while(cont){
            System.out.println("\n\tWhat do you want to do?");
            System.out.println("1. View pending request");
            System.out.println("2. Accept a Request");
            System.out.println("3. Reject a Request");
            System.out.println("9. Return to main menu.");
            switch(readChoice()){
              case 1:{
                  Statement stmt = esql._connection.createStatement();
                  String s = String.format("SELECT * FROM CONNECTION_USR WHERE connectionId = '%s'", authorisedUser);
                  ResultSet rs = stmt.executeQuery (s);
                  int i = 1;
                  while (rs.next())
                  {
                    if (rs.getString(i+2).trim().equals("Request")){
                      String fn = get_full_name(esql,rs.getString(i).trim());
                      System.out.println(fn+" has sent you a friend request.");
                    }
                  }
                  stmt.close();
                  break;
              }
              case 2:{
                System.out.println("Who do you want to accept?");
                String acc = in.readLine();
                acc = acc.trim();
                acc = get_userID(esql,acc);
                String update_req= String.format("UPDATE CONNECTION_USR SET status ='Accept' WHERE userId = '%s' AND connectionId = '%s'",acc, authorisedUser);
                esql.executeUpdate(update_req);
                break;
              }
              case 3:{
                System.out.println("Who do you want to reject?");
                String acc = in.readLine();
                acc = acc.trim();
                String update_req= String.format("UPDATE CONNECTION_USR SET status ='Reject' WHERE userId = '%s' AND connectionId = '%s'",acc, authorisedUser);
                esql.executeUpdate(update_req);
                break;
              }
              case 9:{
                  cont = false;
                  break;
              }
              default: cont = false; break;
            }//end switch
          }//end while
      }
      catch(Exception e){
        System.err.println(e.getMessage());
      }
   }

   public static void send_Request(ProfNetwork esql, String requester, String requestee)
   {
      try
      {
          String is_exist = String.format("SELECT status FROM CONNECTION_USR WHERE userId = '%s' AND connectionId = '%s'",requester,requestee );
          int q = esql.executeQuery (is_exist);

          if (q == 0 ){ //request does not exists
            String r = "Request";
            String query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s', '%s', '%s')",requester,requestee,r);
            esql.executeUpdate(query);
          }
          else{ //connection was already made, check what kind of status
            Statement stmt = esql._connection.createStatement();
            String status = String.format("SELECT status FROM CONNECTION_USR WHERE userId = '%s' AND connectionId = '%s'",requester,requestee);
            ResultSet rs = stmt.executeQuery (status);
            int i = 1;
            while(rs.next()){
              if (!rs.getString(i).equals("Accept") || !rs.getString(i).equals("Request")){ //this is everything but accept and Request
                String r = "Request";
                String send = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s', '%s', '%s')",requester,requestee,r);
              }
            }
          }
      }
      catch (Exception e)
      {
         System.err.println (e.getMessage ());
      }
   }

   public static void view_friends(ProfNetwork esql, String person)
   {
      try
      {
          System.out.println("Friend(s) of "+person+":");
          person = get_userID(esql,person);
          String view_friend = String.format("SELECT name FROM USR WHERE userId IN (SELECT connectionID FROM CONNECTION_USR WHERE userID = '%s' AND status = 'Accept')", person);
          String view_friend_2 = String.format("SELECT name FROM USR WHERE userId IN (SELECT userID FROM CONNECTION_USR WHERE connectionID = '%s' AND status = 'Accept')", person);

          //Print out Friend's friend's List if any
          esql.executeQueryAndPrintResult(view_friend);
          esql.executeQueryAndPrintResult(view_friend_2);
      }
      catch (Exception e)
      {
         System.err.println (e.getMessage ());
      }
   }

   public static void Search(ProfNetwork esql, String cur_user) throws SQLException
   {
      try
      {
      	 System.out.print("Enter the name of the person you want to search for: ");
         String person = in.readLine();
         person=person.trim();
         Statement stmt4 = esql._connection.createStatement ();
         String q4 = String.format("SELECT U.name FROM USR U WHERE U.userID = '%s'", cur_user);
         int is_exist = esql.executeQuery(q4);

         String c_usr = "";
         if (is_exist != 0){
            ResultSet rs4 = stmt4.executeQuery (q4);
            ResultSetMetaData rsmd1 = rs4.getMetaData ();
             while (rs4.next())
             {
               int i = 1;
               c_usr = rs4.getString(i);
               c_usr=c_usr.trim();
             }
             stmt4.close();

             print_prof(esql,c_usr, person);
          }
          else{System.out.println("User Does Not Exist");}
      }
      catch (Exception e)
      {
         System.err.println (e.getMessage ());
      }
   }

   public static void print_prof(ProfNetwork esql, String cur_user, String user) throws SQLException
   {
      try {
            Statement stmt1 = esql._connection.createStatement ();
            Statement stmt2 = esql._connection.createStatement ();
            String q1 = String.format("SELECT W.company, W.role, W.location, W.startdate, W.enddate FROM WORK_EXPR W, USR U WHERE U.name = '%s' AND U.userID = W.userID", user);
            String q2 = String.format("SELECT E.instituitionName, E.major, E.degree, E.startdate, E.enddate FROM EDUCATIONAL_DETAILS E, USR U WHERE U.name = '%s' AND U.userID = E.userID", user);
            ResultSet rs1 = stmt1.executeQuery (q1);
            ResultSet rs2 = stmt2.executeQuery (q2);
            ResultSetMetaData rsmd1 = rs1.getMetaData ();
            ResultSetMetaData rsmd2 = rs2.getMetaData ();
            System.out.println();
            System.out.println("###############################################");
            System.out.println(user + "'s Profile:");
            System.out.print("_______________________________________________\n");
            int i = 1;
            if (cur_user.equals(user))
            {
                Statement stmt3 = esql._connection.createStatement ();
                String q3 = String.format("SELECT userID, email, dateOfBirth FROM USR WHERE name = '%s'",user);
                ResultSet rs3 = stmt3.executeQuery (q3);
                ResultSetMetaData rsmd3 = rs3.getMetaData ();
                while (rs3.next())
                {
                	System.out.println("User ID: "+rs3.getString(i));
                	System.out.println("E-mail: "+rs3.getString(i+1));
                	System.out.println("Date of Birth: "+rs3.getString(i+2)+"\n");
                }
                stmt3.close();
            }
            else if (isFriend(esql,cur_user,user))
            {
               Statement stmt3 = esql._connection.createStatement ();
               String q3 = String.format("SELECT dateOfBirth FROM USR WHERE name = '%s'",user);
               ResultSet rs3 = stmt3.executeQuery (q3);
               ResultSetMetaData rsmd3 = rs3.getMetaData ();
               while (rs3.next())
               {
               		System.out.println("Date of Birth: "+rs3.getString(i)+"\n");
               }
               stmt3.close();
            }
            System.out.println("Work Experience:");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            while (rs1.next())
            {
               System.out.println ("Company: "+rs1.getString (i));
               System.out.println ("Role: "+rs1.getString (i+1));
               System.out.println ("Location: "+rs1.getString (i+2));
               System.out.println ("Duration: "+rs1.getString (i+3)+" to "+rs1.getString (i+4));
            }
            System.out.println ();
            stmt1.close ();
            System.out.println();
            System.out.print("Education:\n");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            while (rs2.next())
            {
                System.out.println ("Insituition: "+rs2.getString (i));
                System.out.println ("Major: "+rs2.getString (i+1));
                System.out.println ("Degree: "+rs2.getString (i+2));
                System.out.println ("Duration: "+rs2.getString (i+3)+" to "+rs2.getString (i+4));
            }
            System.out.println ();
            stmt2.close ();
            System.out.println("###############################################");
            System.out.println();
        }
        catch (Exception e)
        {
          System.err.println (e.getMessage ());
        }
   }

   public static boolean isFriend(ProfNetwork esql, String user, String q_friend)
   {
       try
       {
  		   Statement stmt = esql._connection.createStatement ();
  		   Statement stmt1 = esql._connection.createStatement ();
  		   String q = String.format("SELECT U.userID FROM USR U WHERE U.name = '%s'", user);
  		   String q1 = String.format("SELECT U.userID FROM USR U WHERE U.name = '%s'", q_friend);
  		   ResultSet rs = stmt.executeQuery (q);
  		   ResultSet rs1 = stmt1.executeQuery (q1);
  		   ResultSetMetaData rsmd = rs.getMetaData ();
  		   ResultSetMetaData rsmd1 = rs1.getMetaData ();
  		   String me = "";
  		   String u = "";
  		   while (rs.next())
  		   {
    			  int i = 1;
    			  me = rs.getString(i);
    			  me=me.trim();
  		   }
  		   while (rs1.next())
  		   {
  			  int i = 1;
  			  u = rs1.getString(i);
  			  u=u.trim();
  		   }
  		   String q2 = String.format("SELECT * FROM CONNECTION_USR WHERE userID = '%s' AND connectionID = '%s'", me,u);
  		   String q3 = String.format("SELECT * FROM CONNECTION_USR WHERE userID = '%s' AND connectionID = '%s'", u, me);
  		   int amigo_me = esql.executeQuery (q2);
  		   int amigo_u = esql.executeQuery (q3);
  		   if (amigo_me > 0 || amigo_u > 0)
  		   {
  			   return true;
  		   }
  		   return false;
    }
  	catch (Exception e)
  	{
  			System.err.println (e.getMessage ());
  			return false;
  	}
  }
  public static String get_userID(ProfNetwork esql, String fn) throws SQLException
  {
         Statement stmt4 = esql._connection.createStatement ();
         String q4 = String.format("SELECT U.userId FROM USR U WHERE U.name = '%s'", fn);
         ResultSet rs4 = stmt4.executeQuery (q4);
         ResultSetMetaData rsmd1 = rs4.getMetaData ();
         String c_usr = "";
         while (rs4.next())
         {
           int i = 1;
           c_usr = rs4.getString(i);
           c_usr=c_usr.trim();
         }
         stmt4.close();
         return c_usr;
  }
  public static String get_full_name(ProfNetwork esql, String usrID) throws SQLException
  {
         Statement stmt4 = esql._connection.createStatement ();
         String q4 = String.format("SELECT U.name FROM USR U WHERE U.userId = '%s'", usrID);
         ResultSet rs4 = stmt4.executeQuery (q4);
         ResultSetMetaData rsmd1 = rs4.getMetaData ();
         String c_usr = "";
         while (rs4.next())
         {
           int i = 1;
           c_usr = rs4.getString(i);
           c_usr=c_usr.trim();
         }
         stmt4.close();
         return c_usr;
  }
  public static void Inbox(ProfNetwork esql, String user)
  {
     try
     {
        boolean cont = true;
        while (cont)
        {
        System.out.println("\nInbox");
        System.out.println("===============");
        System.out.println("1. Compose a message");
        System.out.println("2. View Messages");
        System.out.println("3. Delete Messages");
        System.out.println("9. Go Back to Main Menu");
        switch (readChoice())
        {
             case 1:
             {
                 System.out.print("Who do you want to send a message to?");
                 String recv = in.readLine();
                 recv=recv.trim();
                 String proper_recv = get_userID(esql,recv);
                 sendMsg(esql, user, recv);
                 break;
             }
             case 2:
             {
                 view_msgs(esql, user);
                 break;
             }
             case 3:
             {
                delete_msg(esql, user);
                break;
             }
             case 9:
              {
                cont = false;
                break;
              }
             default:{break;}
        }
      }
     }
     catch (Exception e)
   {
    System.err.println (e.getMessage ());
   }
  }

  public static void sendMsg(ProfNetwork esql, String user, String recv) throws SQLException
  {
     try
     {
          Statement stmt = esql._connection.createStatement();
          String q = String.format("SELECT COUNT(*) FROM MESSAGE");
          ResultSet rs = stmt.executeQuery(q);
          rs.next();
          int msg_id = rs.getInt(1);
          ++msg_id;
          stmt.close();
          System.out.print("Type your message:\n");
          String msg = in.readLine();
          int delete_stat = 0;
          String stat = "Sent";
          Calendar calendar = Calendar.getInstance();
          java.util.Date now = calendar.getTime();
          java.sql.Timestamp ts = new java.sql.Timestamp(now.getTime());
          Statement stmt1 = esql._connection.createStatement();
          String query = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES (?,'%s','%s','%s',?,?,'%s')",user, recv, msg, stat);
          PreparedStatement pstmt = esql._connection.prepareStatement(query);
          pstmt.setInt(1, msg_id);
          pstmt.setTimestamp(2, ts);
          pstmt.setInt(3, 0);
          pstmt.executeUpdate();
     }
     catch (Exception e)
     {
        System.err.println (e.getMessage ());
        String q = String.format("SELECT * FROM MESSAGE");
            int msg_id = esql.executeQuery(q);
            String msg = "";
            int delete_stat = 0;
        String stat = "Failed to Deliver";
        java.util.Date now= new java.util.Date();
     }
  }

  public static void view_msgs(ProfNetwork esql, String user) throws SQLException
  {
     try
     {
     Statement stmt = esql._connection.createStatement ();
     //Statement stmt1 = esql._connection.createStatement ();
     String q = String.format("SELECT * FROM MESSAGE WHERE receiverId = '%s' OR senderId = '%s'",user, user);
     //String q1 = String.format("SELECT * FROM MESSAGE WHERE senderId = '%s'", user);
     ResultSet rs = stmt.executeQuery (q);
     //ResultSet rs1 = stmt1.executeQuery (q1);
     ResultSetMetaData rsmd = rs.getMetaData ();
     //ResultSetMetaData rsmd1 = rs.getMetaData ();
     System.out.println ("Messages:");
     int i = 1;
     int d_stat = -999;
     System.out.println ();
     while (rs.next())
     {
        String sender = rs.getString(i+1);
        String receiver = rs.getString(i+2);
        receiver = receiver.trim();
        sender = sender.trim();
        d_stat = rs.getInt(i+5);
        if (d_stat != 3){
          if ( (user.equals(sender) && d_stat != 1) || (user.equals(receiver) && d_stat != 2)) {
             System.out.println ("Message ID: "+rs.getString (i));
             System.out.println ("=========================================");
             String msg = rs.getString(i+3);
             msg = msg.trim();
             System.out.println (msg);
             System.out.println ("Send Time: "+rs.getString (i+4));
             System.out.println ("Status: "+rs.getString (i+6));
             System.out.println ();
             String update_status = String.format("UPDATE MESSAGE SET status='Read' WHERE receiverId = '%s'", user);
                 esql.executeUpdate(update_status);
          }
        }
     }
     stmt.close();
    }
    catch (Exception e)
    {
      System.err.println (e.getMessage ());
    }
  }

  public static void delete_msg(ProfNetwork esql, String user) throws SQLException
  {
       int del_m = -999;
       System.out.println("What message do you want to delete?");
       del_m = readChoice();
       Statement stmt = esql._connection.createStatement ();
       String q = String.format("SELECT * FROM MESSAGE WHERE msgId = "+del_m+" AND (senderId = '%s' OR receiverId = '%s')" ,user, user);
       ResultSet rs = stmt.executeQuery (q);
       ResultSetMetaData rsmd = rs.getMetaData ();
       int i = 1;
       int d_stat = -999;
       String recv = "";
       String send = "";
       int msg_id = -999;
       while (rs.next())
       {
         msg_id = Integer.parseInt(rs.getString(i));
         send = rs.getString(i+1);
         send = send.trim();
         recv = rs.getString(i+2);
         recv = recv.trim();
         d_stat = Integer.parseInt(rs.getString (i+5));
         System.out.println(send);
         System.out.println(recv);
         System.out.println(d_stat);
      }
      if (send.equals(user))
      {
        if (d_stat==0)
        {
          String update_status = String.format("UPDATE MESSAGE SET deleteStatus=1 WHERE msgId = "+del_m);
          esql.executeUpdate(update_status);
        }
        else if (d_stat==2)
        {
           String delete_q = String.format("DELETE FROM MESSAGE WHERE msgId = "+ del_m);
           esql.executeUpdate(delete_q);
        }
        else
        {
           System.out.println("Error: you have already deleted this messsage.");
        }

     }
     else if (recv.equals(user))
     {
      System.out.println(d_stat);
        if (d_stat==0)
        {
           String update_status = String.format("UPDATE MESSAGE SET deleteStatus=2 WHERE msgId = " + del_m);
           esql.executeUpdate(update_status);
        }
        else if (d_stat==1)
        {
           String delete_q = String.format("DELETE FROM MESSAGE WHERE msgId = " + del_m);
           esql.executeUpdate(delete_q);
        }
        else
        {
           System.out.println("Error: you have already deleted this messsage.");
        }
     }
     stmt.close();
  }
  public static void UpdateProfile(ProfNetwork esql,String authorisedUser)
  {
    try
    {
       System.out.println("Would you like to do?");
       System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
       System.out.println("1. Update work experience.");
       System.out.println("2. Update educational details.");
       System.out.println("9. Return to main menu.");
       switch(readChoice())
       {
         case 1:
         {
              System.out.print("Enter a company name: ");
              String comp = in.readLine();
              System.out.print("Enter your role at this company: ");
              String roll= in.readLine();
              System.out.print("Location: ");
              String loc = in.readLine();
              System.out.print("Start Date(YYYY-MM-DD): ");
              String b_date = in.readLine();
              System.out.print("End Date(YYYY-MM-DD): ");
              String e_date = in.readLine();
              String query = String.format("INSERT INTO WORK_EXPR (userId, company, role, location, startdate, enddate) VALUES ('%s','%s','%s','%s','%s','%s')", authorisedUser, comp, roll, loc, b_date,e_date);
              esql.executeUpdate(query);
              break;
         }
         case 2:
         {
           System.out.print("Enter instutution: ");
              String inst = in.readLine();
              System.out.print("Major: ");
              String majr = in.readLine();
              System.out.print("Degree: ");
              String deg = in.readLine();
              System.out.print("Start Date(YY/MM/DD): ");
              String b_date = in.readLine();
              System.out.print("End Date(YY/MM/DD): ");
              String e_date = in.readLine();
              String query = String.format("INSERT INTO EDUCATIONAL_DETAILS (userId, instituitionName, Major, Degree, startdate, enddate) VALUES ('%s','%s','%s','%s','%s','%s')", authorisedUser, inst, majr, deg, b_date, e_date);
              esql.executeUpdate(query);
              break;
         }

       }
     }
    catch (Exception e)
    {
      System.err.println (e.getMessage ());
    }
  }
}//end ProfNetwork
