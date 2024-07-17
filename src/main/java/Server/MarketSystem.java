package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Domain.Entities.Guest;
import Domain.Entities.Order;
import Domain.Entities.Product;
import Domain.Entities.Shop;
import Domain.Entities.ShopOrder;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.ShoppingCart;
import Domain.Entities.User;
import Domain.Entities.enums.Category;
import Domain.ExternalServices.PaymentService.AdapterPaymentImp;
import Domain.ExternalServices.PaymentService.AdapterPaymentInterface;
import Domain.ExternalServices.PaymentService.ProxyApyment;
import Domain.ExternalServices.SupplyService.AdapterSupplyImp;
import Domain.ExternalServices.SupplyService.AdapterSupplyInterface;
import Domain.ExternalServices.SupplyService.ProxySupply;
import Domain.Facades.ShopFacade;
import Domain.Facades.ShoppingCartFacade;
import Domain.Facades.UserFacade;
import Domain.Repositories.InterfaceDiscountRepository;
import Domain.Repositories.InterfaceGuestRepository;
import Domain.Repositories.InterfaceOrderRepository;
import Domain.Repositories.InterfaceProductRepository;
import Domain.Repositories.InterfaceRoleRepository;
import Domain.Repositories.InterfaceShopRepository;
import Domain.Repositories.InterfaceShoppingBasketRepository;
import Domain.Repositories.InterfaceShoppingCartRepository;
import Domain.Repositories.InterfaceUserRepository;
import Domain.Repositories.MemoryDiscountRepository;
import Domain.Repositories.MemoryGuestRepository;
import Domain.Repositories.MemoryOrderRepository;
import Domain.Repositories.MemoryProductRepository;
import Domain.Repositories.MemoryRoleRepository;
import Domain.Repositories.MemoryShopRepository;
import Domain.Repositories.MemoryShoppingBasketRepository;
import Domain.Repositories.MemoryShoppingCartRepository;
import Domain.Repositories.MemoryUserRepository;
import Dtos.BasicDiscountDto;
import Dtos.PaymentInfoDto;
import Dtos.ProductDto;
import Dtos.PurchaseCartDetailsDto;
import Dtos.ShopDto;
import Dtos.ShopManagerDto;
import Dtos.SupplyInfoDto;
import Dtos.UserDto;
import Exceptions.StockMarketException;

@Service
public class MarketSystem {

    public String external_system_url = "https://damp-lynna-wsep-1984852e.koyeb.app/";
    public String tests_config_file_path = "src/main/java/Server/Configuration/test_config.txt";
    public String instructions_config_path = "src/main/java/Server/Configuration/test_loading.txt";
    public String real_system_config_path = "src/main/java/Server/Configuration/system_config.txt";

    private AdapterPaymentInterface payment_adapter;
    private AdapterSupplyInterface supply_adapter;

    private static final Logger logger = Logger.getLogger(MarketSystem.class.getName());

    private ShopFacade shopFacade;
    private UserFacade userFacade;
    private ShoppingCartFacade shoppingCartFacade;
    
    @Autowired
    public MarketSystem(ShopFacade shopFacade, UserFacade userFacade, ShoppingCartFacade shoppingCartFacade) throws StockMarketException {
        this.shopFacade = shopFacade;
        this.userFacade = userFacade;
        this.shoppingCartFacade = shoppingCartFacade;
        this.init_market(real_system_config_path);
    }

    // for test - set facades and urls to check
    public MarketSystem(ShopFacade shopFacade, UserFacade userFacade, ShoppingCartFacade shoppingCartFacade, String external_system_url, String instructions_config_path, String real_system_config_path) {
        this.shopFacade = shopFacade;
        this.userFacade = userFacade;
        this.shoppingCartFacade = shoppingCartFacade;
        this.external_system_url = external_system_url;
        this.instructions_config_path = instructions_config_path;
        this.real_system_config_path = real_system_config_path;
    }

    // for test - set instruction config file path
    public void setInstructions_config_path(String instructions_config_path) {
        this.instructions_config_path = instructions_config_path;
    }

    // for test - set init config file path
    public void setReal_system_config_path(String real_system_config_path) {
        this.real_system_config_path = real_system_config_path;
    }

    // initiate the system using the args config files
    public void init_market(String config_file_path) throws StockMarketException{
        logger.info("Start Init Market");
        logger.info("Configuration File Path: "+config_file_path);
        String[] instructions;
        instructions = read_config_file(config_file_path);
        String external_services_instruction = instructions[0];
        set_external_services(external_services_instruction);
        connect_to_external_services();
        String database_instruction = instructions[1];
        set_database(database_instruction);
    }

    /**
     * reading the data from the configuration file.
     * @param config_path the path of the configuration file.
     * @return the 2 config instructions, 1) external services 2) database
     * @throws StockMarketException if the format file is unmatched.
     */
    public String[] read_config_file(String config_path) throws StockMarketException {
        String[] to_return = new String[2];
        int counter = 0;
        try {
            File file = new File(config_path);
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String instruction = scanner.nextLine();
                if (!instruction.equals("")) {
                    if (counter > 1){
                        throw new StockMarketException("Config File - Illegal Format.");
                    }
                    to_return[counter]  = instruction;
                    counter++;
                }
            }
        }
        catch (FileNotFoundException e) {throw new StockMarketException("Config File - File Not Found");}
        if (counter != 2) {throw new StockMarketException("Config File - Format File Unmatched.");}
        return to_return;
    }

    /** Connect the system to the external services after set the services according the configuration file.
     * @throws StockMarketException if the handshake fail.
     */
    private void connect_to_external_services() throws StockMarketException {
        logger.info("System Start Connect To External Services");
        //boolean connect_to_external_systems = payment_adapter.handshake() && supply_adapter.handshake();
        boolean connect_to_external_systems = payment_adapter.ConnectToService() && supply_adapter.ConnectToService();
        if (!connect_to_external_systems) // have to exit
        {
            throw new StockMarketException("Cant Connect To The External Systems");
        }
    }

    /**
     * Requirement 1.3 & 1.4
     *
     * this method crate adapters to the external services.
     * @param config - "external_services:demo" or "external_services:real"
     * @throws StockMarketException if the input is illegal.
     */
    public void set_external_services(String config) throws StockMarketException {
        if (config.equals("external_services:tests")){
            logger.info("Set Tests External Services");
            payment_adapter = ProxyApyment.getProxyApymentPayment();
            supply_adapter = ProxySupply.getProxySupply();
        }
        else if (config.equals("external_services:real")){
            logger.info("Set Real External Services");
            payment_adapter = AdapterPaymentImp.getRealAdapterPayment();
            supply_adapter = AdapterSupplyImp.getAdapterSupply();
        }
        else {
            throw new StockMarketException("System Config File - Illegal External Services Data.");
        }
    }

    /**
     * this method init system database,
     * if the demo option on, the system will init data from the data config file,
     *      the init can failed and system keep running without data.
     * if the real option on, the method will try to connect the real database.
     * @param config - configuration instruction - "database:demo" or "database:real".
     * @throws StockMarketException if the connection to DB fail OR wrong format of the config instruction.
     */
    public void set_database(String config) throws StockMarketException{
        // database:real/demo
        if (config.equals("database:tests")){
            // no db
            logger.info("Init Data For Tests: No Database");

            InterfaceShoppingCartRepository shoppingCartRepository = new MemoryShoppingCartRepository();
            InterfaceShoppingBasketRepository shoppingBasketRepository = new MemoryShoppingBasketRepository();
            InterfaceShopRepository shopRepository = new MemoryShopRepository();
            InterfaceProductRepository productRepository = new MemoryProductRepository();
            InterfaceUserRepository userRepository = new MemoryUserRepository();
            InterfaceGuestRepository guestRepository = new MemoryGuestRepository();
            InterfaceOrderRepository orderRepository = new MemoryOrderRepository();
            InterfaceRoleRepository roleRepository = new MemoryRoleRepository();
            InterfaceDiscountRepository discountRepository = new MemoryDiscountRepository();

            shoppingCartFacade.setShoppingCartFacadeRepositories(shoppingCartRepository, orderRepository, guestRepository, userRepository, shoppingBasketRepository);
            shopFacade.setShopFacadeRepositories(shopRepository, productRepository, roleRepository, discountRepository);
            userFacade.setUserFacadeRepositories(userRepository, guestRepository, orderRepository, shoppingCartRepository);
        
        }
        else if (config.equals(("database:real_init"))){            
            logger.info("Init Data From Instructions File, Data File Path: " + instructions_config_path);
        }
        else {
            throw new StockMarketException("System Config File - Illegal Database Data.");
        }
        init_data_to_market(instructions_config_path);
    }

    /**
     * init date from the instructions configuration file.
     * this method should keep the logic order of system instructions.
     * "" is legal input -> the method wouldn't do anything and keep going.
     * @param instructions_config_path - location of the instruction config file.
     * @return true if the system load data successfully.
     *  false if was illegal instructions order OR illegal format instruction.
     * @throws StockMarketException 
     */
    public void init_data_to_market(String instructions_config_path) throws StockMarketException{ 
        logger.info("Start to Init Data From Instructions File");
        try{
            File file = new File(instructions_config_path);
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()){
                String instruction = scanner.nextLine();
                if (!instruction.equals("")){
                    String[] instruction_params = instruction.split("#");
                    run_instruction(instruction_params);
                }
            }
        } catch (Exception e) {
            logger.info("Init Data Demo Fail, The System Run With No Data :" + e.getMessage());
            throw new StockMarketException("Init Data Demo Fail, The System Run With No Data :" + e.getMessage());
        }
    }

    /**
     * execute instructions from the init data config file.
     * @param instruction_params - instruction to execute.
     * @param facades - the data structure who managed the initialization of data.
     * @throws Exception in bad format or bad logical order of instructions.
     */
    private void run_instruction(String[] instruction_params) throws Exception {
        String instruction = instruction_params[0];

        // handle instructions : User Facade ----------------------------------------------------------------------------------------------------------------------------------------

        if (instruction.equals("logIn")){
            //logIn#user_name#password
            try {
                userFacade.logIn(instruction_params[1], instruction_params[2]);
            } catch (Exception e) {
                logger.info("[run_instruction] LogIn Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("logOut")){
            //logOut#user_name
            try {
                userFacade.logOut(instruction_params[1]);    
            } catch (Exception e) {
                logger.info("[run_instruction] LogOut Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("register")){
            //register#user_name#password#email#birthdate
            try {
                LocalDate localdate = LocalDate.parse(instruction_params[4], DateTimeFormatter.ISO_LOCAL_DATE);
                @SuppressWarnings("deprecation")
                Date birthdate = new Date(localdate.getYear(), localdate.getMonthValue(), localdate.getDayOfMonth());
                UserDto userDto = new UserDto(instruction_params[1], instruction_params[2], instruction_params[3], birthdate);
                userFacade.register(userDto);
            } catch (Exception e) {
                logger.info("[run_instruction] Register Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("add_order_to_user")){
            //add_order_to_user#???
            try {
                // TODO: INBAR: need to implement
                List<ShoppingBasket> shoppingBasket = new ArrayList<>();
                Order order = new Order(shoppingBasket, Integer.parseInt(instruction_params[0]), Integer.parseInt(instruction_params[0]));
                userFacade.addOrderToUser(instruction_params[1], order);
            } catch (Exception e) {
                logger.info("[run_instruction] add order to user Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("add_new_guest")){
            //add_new_guest#string_guest_id
            try {
                userFacade.addNewGuest(instruction_params[1]);
            } catch (Exception e) {
                logger.info("[run_instruction] add new guest Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("remove_guest")){
            //remove_guest#string_guest_id
            try {
                userFacade.removeGuest(instruction_params[1]);
            } catch (Exception e) {
                logger.info("[run_instruction] remove guest Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("change_email")){
            //change_email#user_name#new_email
            try {
                userFacade.changeEmail(instruction_params[1], instruction_params[2]);
            } catch (Exception e) {
                logger.info("[run_instruction] change email Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("set_user_details")){
            //set_user_details#user_name#new_name#new_password#new_email#new_birthdate
            try {
                LocalDate localdate = LocalDate.parse(instruction_params[5], DateTimeFormatter.ISO_LOCAL_DATE);
                @SuppressWarnings("deprecation")
                Date birthdate = new Date(localdate.getYear(), localdate.getMonthValue(), localdate.getDayOfMonth());
                UserDto userDto = new UserDto(instruction_params[2], instruction_params[3], instruction_params[4], birthdate);
                userFacade.setUserDetails(instruction_params[1], userDto);
            } catch (Exception e) {
                logger.info("[run_instruction] set user details Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("notify_user")){
            //notify_user#target_user_name#???
            try {
                // TODO: INBAR: implement
                // Alert alert = new Alert("New User Details", "Your details has been changed");
                // userFacade.notifyUser(instruction_params[1], null)
            } catch (Exception e) {
                logger.info("[run_instruction] set user details Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("report_to_admin")){
            //report_to_admin#user_name#message
            try {
                userFacade.reportToAdmin(instruction_params[1], instruction_params[2]);
            } catch (Exception e) {
                logger.info("[run_instruction] report to admin Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("add_admin")){
            //add_admin#user_name#password#email#birthdate
            try {
                LocalDate localdate = LocalDate.parse(instruction_params[4], DateTimeFormatter.ISO_LOCAL_DATE);
                @SuppressWarnings("deprecation")
                Date birthdate = new Date(localdate.getYear(), localdate.getMonthValue(), localdate.getDayOfMonth());
                UserDto userDto = new UserDto(instruction_params[1], instruction_params[2], instruction_params[3], birthdate);
                userFacade.register(userDto);
                User user = userFacade.getUserByUsername(instruction_params[1]);
                userFacade.setSystemAdmin(user);
            } catch (Exception e) {
                logger.info("[run_instruction] Add Admin Fail: " + e.getMessage());
            }
        }

        // handle instructions : Shopping Cart Facade -------------------------------------------------------------------------------------------------------------------------------

        else if (instruction.equals("add_product_to_cart")){
            //add_product_to_cart#user_name#product_name#shop_name#quantity
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[3]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[2], shopId);
                shoppingCartFacade.addProductToUserCart(instruction_params[1], productId, shopId, Integer.parseInt(instruction_params[4]));
            } catch (Exception e) {
                logger.info("[run_instruction] Add Product To Cart Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("add_cart_for_guest")){
            //add_cart_for_guest#user_name
            try {
                shoppingCartFacade.addCartForGuest(instruction);
            } catch (Exception e) {
                logger.info("[run_instruction] add cart for guest Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("add_cart_for_user")){
            //add_cart_for_user#user_name#user_name
            try {
                User user = userFacade.getUserByUsername(instruction_params[2]);
                shoppingCartFacade.addCartForUser(instruction_params[1], user);
            } catch (Exception e) {
                logger.info("[run_instruction] add cart for user Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("remove_product_from_cart")){
            //remove_product_from_cart#user_name#product_name#shop_name#quantity
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[3]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[2], shopId);
                shoppingCartFacade.removeProductFromUserCart(instruction_params[1], productId, shopId, Integer.parseInt(instruction_params[4]));
            } catch (Exception e) {
                logger.info("[run_instruction] Remove Product From Cart Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("purchase_cart")){
            //purchase_cart#user_name#card_number#month#year#holder#cvv#user_id#address#city#country#zip
            try {
                ShoppingCart shoppingCart = (ShoppingCart) shoppingCartFacade.getCartByUsernameOrToken(instruction_params[1]);
                //String currency, String cardNumber, String month, String year, String holder, String cvv, String id
                PaymentInfoDto paymentInfo = new PaymentInfoDto("" + shoppingCart.getTotalPrice(), instruction_params[2], instruction_params[3], instruction_params[4], instruction_params[5], instruction_params[6], instruction_params[7]);
                //String name ,String address,String city,String country,String zip
                SupplyInfoDto supplyInfo = new SupplyInfoDto(instruction_params[1], instruction_params[8], instruction_params[9], instruction_params[10], instruction_params[11]);
                List<Integer> basketsToBuy = shoppingCart.getShoppingBasketIdList();
                PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfo, supplyInfo, basketsToBuy);
                shoppingCartFacade.purchaseCartUser(instruction_params[1], purchaseCartDetailsDto);
            } catch (Exception e) {
                logger.info("[run_instruction] Purchase Cart Fail: " + e.getMessage());
            }
        }

        // handle instructions : Shop Facade ----------------------------------------------------------------------------------------------------------------------------------------

        else if (instruction.equals("open_shop")){
            //open_shop#user_name#shop_name#bank_details#shop_address
            try {
                ShopDto shopDto = new ShopDto(instruction_params[2], instruction_params[3], instruction_params[4]);
                shopFacade.openNewShop(instruction_params[1], shopDto);
            } catch (Exception e) {
                logger.info("[run_instruction] Open New Shop Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("close_shop")){
            //close_shop#user_name#shop_name
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.closeShop(shopId, instruction_params[1]);
            } catch (Exception e) {
                logger.info("[run_instruction] Close Shop Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("reopen_shop")){
            //reopen_shop#user_name#shop_name
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.reOpenShop(shopId, instruction_params[1]);
            } catch (Exception e) {
                logger.info("[run_instruction] Reopen Shop Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("add_product_to_shop")){
            //add_product_to_shop#user_name#shop_name#category#product_name#price#quantity
            try {
                ProductDto productDto = new ProductDto(instruction_params[3], Category.valueOf(instruction_params[4]), Integer.parseInt(instruction_params[5]), Integer.parseInt(instruction_params[6]));
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.addProductToShop(shopId, productDto, instruction_params[1]);
            } catch (Exception e) {
                logger.info("[run_instruction] Add Product To Shop Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("remove_product_from_shop")){
            //remove_product_from_shop#user_name#shop_name#product_name#category
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                Product product = shopFacade.getProductInShopByName(shopId, instruction_params[3]).get(shopId).get(0);
                ProductDto productDto = new ProductDto(product.getProductId(), instruction_params[3], Category.valueOf(instruction_params[4]), Integer.parseInt(instruction_params[5]), Integer.parseInt(instruction_params[6]));
                shopFacade.removeProductFromShop(shopId, productDto, instruction_params[1]);
            } catch (Exception e) {
                logger.info("[run_instruction] Remove Product From Shop Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("open_complaint")){
            //open_complaint#user_name#shop_name#message
            try {
                // TODO: INBAR: need to check
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.openComplaint(shopId, instruction_params[1], instruction_params[3]);
            } catch (Exception e) {
                logger.info("[run_instruction] Open Complint Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("edit_quantity_product")){
            //edit_quantity_product#user_name#shop_name#product_name#quantity
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                shopFacade.updateProductQuantity(instruction_params[1], shopId, productId, Integer.parseInt(instruction_params[4]));
            } catch (Exception e) {
                logger.info("[run_instruction] edit quantity product Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("edit_price_product")){
            //edit_price_product#user_name#shop_name#product_name#new_price
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                shopFacade.updateProductPrice(instruction_params[1], shopId, productId, Double.parseDouble(instruction_params[4]));
            } catch (Exception e) {
                logger.info("[run_instruction] edit price product Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("edit_name_product")){
            //edit_name_product#user_name#shop_name#product_name#new_name
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                shopFacade.updateProductName(instruction_params[1], shopId, productId, instruction_params[4]);
            } catch (Exception e) {
                logger.info("[run_instruction] edit name product Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("edit_category_product")){
            //edit_category_product#user_name#shop_name#product_name#new_category
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                shopFacade.updateProductCategory(instruction_params[1], shopId, productId, Category.valueOf(instruction_params[4]));
            } catch (Exception e) {
                logger.info("[run_instruction] edit category product Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("add_basic_dicsount_to_shop")){
            //add_basic_dicsount_to_shop#user_name#shop_name#product_name#is_precentage#discount_amount#expiration_date#discount_category
            try {
                // TODO: INBAR: need to test
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                // BasicDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, Category category, int id)
                BasicDiscountDto discountDto = new BasicDiscountDto(productId, Boolean.parseBoolean(instruction_params[4]), Double.parseDouble(instruction_params[5]), new Date(), Category.valueOf(instruction_params[7]), -1);
                shopFacade.addBasicDiscountToShop(shopId, instruction_params[1], discountDto);
            } catch (Exception e) {
                logger.info("[run_instruction] add basic dicsount to shop Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("add_conditional_dicsount_to_shop")){
            //add_vasic_dicsount_to_shop#???
            try {
                // TODO: INBAR: need to implement
                // ConditionalDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, Category category, List<Integer> mustHaveProducts, int id)
                // shopFacade.addConditionalDiscountToShop(int shopId, String username, ConditionalDiscountDto discountDto)
            } catch (Exception e) {
                logger.info("[run_instruction] add conditional dicsount to shop Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("remove_discount_from_shop")){
            //remove_discount_from_shop#user_name#shop_name#discount_id
            try {
                // TODO: INBAR: need to test
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.removeDiscountFromShop(shopId, Integer.parseInt(instruction_params[1]), instruction_params[3]);
            } catch (Exception e) {
                logger.info("[run_instruction] remove discount from shop Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("appoint_shop_owner")){
            //appoint_shop_owner#founder_user_name#shop_name#owner_user_name
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.addShopOwner(instruction_params[1], shopId, instruction_params[3]);
            } catch (Exception e) {
                logger.info("[run_instruction] Appoint Shop Owner Fail: " + e.getMessage());
            }

        }

        else if (instruction.equals("notify_appint_shop_owner")){
            //notify_appint_shop_owner#founder_user_name#shop_name#owner_user_name
            try {
                // TODO: INBAR: need to test this
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.notifyAppointOwner(instruction_params[1], instruction_params[3], shopId);
            } catch (Exception e) {
                logger.info("[run_instruction] notiffy appint shop owner Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("appoint_shop_manager")){
            //appoint_shop_manager#founder_user_name#shop_name#manager_user_name#permission1#permission2#...
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                Set<String> permissions = new HashSet<>();
                for (int i = 4; i < instruction_params.length; i++){
                    permissions.add(instruction_params[i]);
                }
                shopFacade.addShopManager(instruction_params[1], shopId, instruction_params[3], permissions);
            } catch (Exception e) {
                logger.info("[run_instruction] Add Shop Manager Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("notify_appint_shop_manager")){
            //notify_appint_shop_manager#founder_user_name#shop_name#manager_user_name#permission1#permission2#...
            try {
                // TODO: INBAR: need to test this
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                Set<String> permissions = new HashSet<>();
                for (int i = 4; i < instruction_params.length; i++){
                    permissions.add(instruction_params[i]);
                }
                shopFacade.notifyAppointManager(instruction_params[1], instruction_params[3], permissions, shopId);
            } catch (Exception e) {
                logger.info("[run_instruction] notiffy appint shop manager Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("fire_shop_manager")){
            //fire_shop_manager#user_name#shop_name#manager_to_fire_user_name
            try {
                // TODO: INBAR: need to test this
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.fireShopManager(instruction_params[1], shopId, instruction_params[3]);
            } catch (Exception e) {
                logger.info("[run_instruction] fire shop manager Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("notify_fire_shop_manager")){
            //notify_fire_shop_manager#user_name#shop_name#manager_to_fire_user_name
            try {
                // TODO: INBAR: need to test this
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.notifyFireUser(instruction_params[1], instruction_params[3], shopId);
            } catch (Exception e) {
                logger.info("[run_instruction] notify fire shop manager Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("resign_from_role")){
            //resign_from_role#user_name#shop_name
            try {
                // TODO: INBAR: need to test this
                int shopId = shopFacade.getShopIdByShopNameAndFounder(instruction_params[1], instruction_params[2]);
                shopFacade.resignFromRole(instruction_params[1], shopId);
            } catch (Exception e) {
                logger.info("[run_instruction] resign from role Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("modify_namager_permissions")){
            //modify_namager_permissions#user_name#shop_name#manager_user_name#permission1#permission2#...
            try {
                // TODO: INBAR: need to test this
                int shopId = shopFacade.getShopIdByShopNameAndFounder(instruction_params[1], instruction_params[2]);
                Set<String> permissions = new HashSet<>();
                for (int i = 4; i < instruction_params.length; i++){
                    permissions.add(instruction_params[i]);
                }
                shopFacade.modifyManagerPermissions(instruction_params[1], shopId, instruction_params[3], permissions);
            } catch (Exception e) {
                logger.info("[run_instruction] modify namager permissions Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("add_product_rating")){
            //add_product_rating#user_name#shop_name#product_name#product_rating
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                shopFacade.addProductRating(shopId, productId, Integer.parseInt(instruction_params[4]));
            } catch (Exception e) {
                logger.info("[run_instruction] add product rating Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("add_shop_rating")){
            //add_shop_rating#user_name#shop_name#shop_rating
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                shopFacade.addShopRating(shopId, Integer.parseInt(instruction_params[3]));
            } catch (Exception e) {
                logger.info("[run_instruction] add shop rating Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("change_shop_policy")){
            //change_shop_policy#???
            try {
                // TODO: INBAR: need to implement
                // shopFacade.changeShopPolicy(String username, int shopId,  List<ShoppingBasketRuleDto> rules)
            } catch (Exception e) {
                logger.info("[run_instruction] change_shop_policy Fail: " + e.getMessage());
            }
        }
        

        else if (instruction.equals("change_product_policy")){
            //change_product_policy#???
            try {
                // TODO: INBAR: need to implement
                // shopFacade.changeProductPolicy(String username, int shopId, int productId, List<UserRuleDto> productRules)
            } catch (Exception e) {
                logger.info("[run_instruction] change_product_policy Fail: " + e.getMessage());
            }
        }
        

        else if (instruction.equals("add_key_word_to_product")){
            //add_key_word_to_product#user_name#shop_name#product_name#keyword1#keyword2#...
            try {
                // TODO: INBAR: need to test this - its enters exha words to db - why ???
                int shopId = shopFacade.getShopIdByShopNameAndFounder(instruction_params[1], instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                List<String> keywords = new ArrayList<>();
                for (int i = 4; i < instruction_params.length; i++){
                    keywords.add(instruction_params[i]);
                }
                shopFacade.addKeywordsToProductInShop(instruction_params[1], shopId, productId, keywords);
            } catch (Exception e) {
                logger.info("[run_instruction] add_key_word_to_product Fail: " + e.getMessage());
            }
        }
        

        else if (instruction.equals("add_shop_discount")){
            //add_shop_discount#user_name#shop_name#product_name#is_precentage#discount_amount#expiration_date#discount_category
            try {
                // TODO: INBAR: need to test
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                // BasicDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, Category category, int id)
                BasicDiscountDto discountDto = new BasicDiscountDto(productId, Boolean.parseBoolean(instruction_params[4]), Double.parseDouble(instruction_params[5]), new Date(), Category.valueOf(instruction_params[7]), -1);
                shopFacade.addShopDiscount(discountDto, shopId);
            } catch (Exception e) {
                logger.info("[run_instruction] add_shop_discount Fail: " + e.getMessage());
            }
        }
        

        else if (instruction.equals("delete_shop_discount")){
            //delete_shop_discount#user_name#shop_name#product_name#is_precentage#discount_amount#expiration_date#discount_category
            try {
                // TODO: INBAR: need to test
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                int productId = shopFacade.getProductIdByProductNameAndShopId(instruction_params[3], shopId);
                // BasicDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, Category category, int id)
                BasicDiscountDto discountDto = new BasicDiscountDto(productId, Boolean.parseBoolean(instruction_params[4]), Double.parseDouble(instruction_params[5]), new Date(), Category.valueOf(instruction_params[7]), -1);
                shopFacade.deleteShopDiscount(discountDto, shopId);
            } catch (Exception e) {
                logger.info("[run_instruction] delete_shop_discount Fail: " + e.getMessage());
            }
        }
        

        else if (instruction.equals("update_user_permissions")){
            //update_user_permissions#user_name#shop_name#user_name_to_update#permission1#permission2#...
            try {
                // TODO: INBAR: need to test this
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                Set<String> permissions = new HashSet<>();
                for (int i = 4; i < instruction_params.length; i++){
                    permissions.add(instruction_params[i]);
                }
                shopFacade.updatePermissions(instruction_params[1], shopId, instruction_params[3], permissions);
            } catch (Exception e) {
                logger.info("[run_instruction] update_user_permissions Fail: " + e.getMessage());
            }
        }

        else if (instruction.equals("get_all_users")){
            //get_all_users
            try {
                List<User> users = userFacade.get_registeredUsers();
                for (User user : users){
                    logger.info("[get_all_users] found register user: " + user.toString());
                }
            } catch (Exception e) {
                logger.info("[run_instruction] get_all_users Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_user_by_username")){
            //get_user_by_username#user_name
            try {
                User user = userFacade.getUserByUsername(instruction_params[1]);
                logger.info("[get_user_by_username] found register user: " + user.toString());
            } catch (Exception e) {
                logger.info("[run_instruction] get_user_by_username Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_guest_by_id")){
            //get_guest_by_id#guest_id
            try {
                Guest guest = userFacade.getGuestById(instruction_params[1]);
                logger.info("[get_guest_by_id] found guest: " + guest.toString());
            } catch (Exception e) {
                logger.info("[run_instruction] get_guest_by_id Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_product_by_id")){
            //get_product_by_id#product_id
            try {
                Product product = shopFacade.getProductById(Integer.parseInt(instruction_params[1]));
                logger.info("[get_product_by_id] found product by id: " + product.toString());
            } catch (Exception e) {
                logger.info("[run_instruction] get_product_by_id Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_shop_dto_by_id")){
            //get_shop_dto_by_id#shop_id
            try {
                ShopDto shop = shopFacade.getShopDtoById(Integer.parseInt(instruction_params[1]));
                logger.info("[get_shop_dto_by_id] found shop dto by id: " + shop.toString());
            } catch (Exception e) {
                logger.info("[run_instruction] get_shop_dto_by_id Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_shop_discounts")){
            //get_shop_discounts#user_name#shop_name
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                List<BasicDiscountDto> shopDiscountDtos = shopFacade.getShopDiscounts(instruction_params[1], shopId);
                for(BasicDiscountDto discountDto : shopDiscountDtos){
                    logger.info("[get_shop_discounts] found shop discount: " + discountDto.toString());
                }
            } catch (Exception e) {
                logger.info("[run_instruction] get_shop_dto_by_id Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_my_subordinates")){
            //get_my_subordinates#user_name#shop_name
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                List<ShopManagerDto> subordinates = shopFacade.getMySubordinates(instruction_params[1], shopId);
                for(ShopManagerDto shopManagerDto : subordinates){
                    logger.info("[get_my_subordinates] found subordinatet shop manager dto: " + shopManagerDto.toString());
                }
            } catch (Exception e) {
                logger.info("[run_instruction] get_my_subordinates Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_shop_managers")){
            //get_shop_managers#user_name#shop_name
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[2]);
                List<ShopManagerDto> shopManagers = shopFacade.getShopManagers(instruction_params[1], shopId);
                for(ShopManagerDto shopManagerDto : shopManagers){
                    logger.info("[get_shop_managers] found shop manager: " + shopManagerDto.toString());
                }
            } catch (Exception e) {
                logger.info("[run_instruction] get_shop_managers Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_all_shop")){
            //get_all_shop
            try {
                List<Shop> shops = shopFacade.getAllShops();
                for(Shop shopEntity : shops){
                    logger.info("[get_all_shop] found shop: " + shopEntity.toString());
                }
            } catch (Exception e) {
                logger.info("[run_instruction] get_all_shop Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_purchase_history")){
            //get_purchase_history#shop_name
            try {
                int shopId = shopFacade.getShopIdByShopName(instruction_params[1]);
                List<ShopOrder> shopOrders = shopFacade.getPurchaseHistory(shopId);
                for(ShopOrder shopOrder : shopOrders){
                    logger.info("[get_purchase_history] found shop order: " + shopOrder.toString());
                }
            } catch (Exception e) {
                logger.info("[run_instruction] get_purchase_history Fail: " + e.getMessage());
            }
        }
        
        else if (instruction.equals("get_shop_by_shop_id")){
            //get_shop_by_shop_id#shop_id
            try {
                Shop shop = shopFacade.getShopByShopId(Integer.parseInt(instruction_params[1]));
                logger.info("[get_shop_by_shop_id] found shop by shop id: " + shop.toString());
            } catch (Exception e) {
                logger.info("[run_instruction] get_shop_by_shop_id Fail: " + e.getMessage());
            }
        }
        
        else{
            throw new Exception("Illegal Instruction Input: " + instruction_params[1]);
        }
    }
}
