package model;


import exceptions.PrivateKeyException;
import exceptions.PublicKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;


public class CipherTest {

    CipherObj testObj;
    private final String testString = "test 323lfdsn";

    CipherObj invalidCipher;
    // hardcoded example public key and private key
    String pubkey1 = "24929200819210665355564229519797990023590892395733576359303052796657782020451015052143112507" +
            "2197" +
            "728392407607589157635707866607824863444659217351002904265085768041043979122470762307186702660972949" +
            "8239147433588448008766293209987542157686706073316270046166191645801704549728728028979309356387111112" +
            "095920820586121227113623269390554456201148281123836957567261602677023670580398980663240223578181997" +
            "7601515759042967180724673255548941686110233074732245171778266640024165472407820265185940251273897708" +
            "5308861658829054811512252262105836471552671848210019145643287660478414488388546964922713836442903453" +
            "90746919333326389404681";
    String privMod = "18058123764724033983115843357237085517544543310896207031483567714623300336099017780640009141" +
            "154613871947071892213047753906548067719246700774639178325878112397975248759030229568559398815468463372" +
            "963106141328974982271694086507624790560121121084571760090588767722199008864971081395292367806613923315" +
            "9091608264867349826278263939629964738015634222919362666668101446999345224202877327431360305628992217534" +
            "0623686830333705828760898442930670531891637070667749300942628553210369631063080393880840725330415840567" +
            "503157120411840330888326577629757717897459492281189716876264985277745689806652358660268152375181246" +
            "71513" +
            "22027251377";
    String privExp = "773801500960900020366880127258325941970495155103068773464078355935984471884078789590175590446" +
            "6866371147437629873491616536576123193914965559213757799802173283371819138804735294167471763046841584187" +
            "9077432128660821694002629261886719456166965747292176728261037878223715756745484050840832676677114985531" +
            "3736500326915662927348896556219752643377049045136257935985484969249205164449682301278841966708385694" +
            "1033" +
            "044651808225240215991135019763724103354406738904732856467687890938274373420114861581501170702950852" +
            "66847" +
            "371225022910638428246408971231854925482678287039361709514340857595506171547987055983357037322558707132" +
            "977" +
            "6833";

    String invalidPub = "17250996A64411591914539129084015705832284460398449270610431346579736693972224842801244" +
            "830918811503224924767850315558126854728329135498341443650291972504356345600828837678531646034371829" +
            "6371954269979477529251575253327565679488473976851457133804662674062905594732456851873808205007432128" +
            "484268830299539762727274686617461753605492350254093920059992869533607124087568435493504495168703003769" +
            "2469281864262350251552219968228813262309071705493496733573345649689319076725809161376012952520912045357" +
            "12794934251754854160812632841115537694273901696651807964258180283984789519088519678898350830291269234762" +
            "9528387788010141323829A";


    @BeforeEach
    void setup() throws NoSuchPaddingException, NoSuchAlgorithmException {
        // Requires exceptions but wont throw them as Algorithm/padding is currently hardcoded
        testObj = new CipherObj();
        //Cipher object for invalid tests
        invalidCipher = new CipherObj();
    }


    // Having a known output for an encryption would defeat the point
    // this test therefor makes sure the output encrypt is the expected byte size
    @Test
    void encryptionTest() {
        testObj.genKeyPair("RSA");
        assertEquals(testObj.encryptText(testString).getClass().getSimpleName(), "SealedObject");
    }

    // Uses same encrypted text used in the first test
    @Test
    void decryptTest() throws Exception {
        // test a string can be encrypted than decrypted
        testObj.genKeyPair("RSA");
        SealedObject sealedTest = testObj.encryptText(testString);
        String output = testObj.decryptText(sealedTest);
        assertEquals(output, testString);
        // empty string test
        SealedObject sealedTest1 = testObj.encryptText("");
        String output1 = testObj.decryptText(sealedTest1);
        assertEquals(output1, "");
    }

    // checks if valid keyPair has been generated
    @Test
    void keyGenTest() {

        try {
            invalidCipher.genKeyPair("s");
        } catch (Exception e) {
        }
        // Tests that exception is not thrown as it
        try {
            testObj.genKeyPair("RSA");
        } catch (Exception e) {
            fail();
        }

        boolean valid = testObj.validPair(testObj.getPublicKey(), testObj.getPrivateKey());
        boolean invalid = testObj.validPair(invalidCipher.getPublicKey(), invalidCipher.getPrivateKey());
        assertTrue(valid);
        assertFalse(invalid);
    }


    // checks to make sure it will only validate keys that are a pair.
    @Test
    void validPairTest() throws PublicKeyException, PrivateKeyException {

        testObj.genKeyPair("RSA");
        try {
            testObj.validPair(invalidCipher.getPublicKey(), testObj.getPrivateKey());
        } catch (Exception e) {
            fail("Invalid public Key");
        }
        try {
            testObj.validPair(testObj.getPublicKey(), invalidCipher.getPrivateKey());
        } catch (Exception e) {
            fail("Invalid private key");
        }
        // valid key pair test
        PublicKey pubKey = testObj.getPublicKey();
        PrivateKey privKey = testObj.getPrivateKey();
        assertTrue(testObj.validPair(pubKey, privKey));
        // Invalid Key pair
        invalidCipher.createPublicKey(pubkey1);
        invalidCipher.createPrivateKey(privMod, privExp);
        PublicKey pubKey2 = invalidCipher.getPublicKey();
        PrivateKey privKey2 = invalidCipher.getPrivateKey();  //different priv key
        assertFalse(testObj.validPair(pubKey2, privKey2));
    }

    @Test
    void createPublicKeyTest() throws PublicKeyException {
        try {
            invalidCipher.createPublicKey("fdsf4d");
            fail("Invalid public key string, exception should have been thrown ");
        } catch (PublicKeyException e) {
            //Pass
        }
        try {
            testObj.createPublicKey(pubkey1);
        } catch (PublicKeyException e) {
            fail("valid public key string, no expected exception ");
        }

        // Valid key test
        assertNotNull(testObj.createPublicKey(pubkey1));
        // Invalid/incorrect format string input

        // invalid key creation of correct length
        try {
            invalidCipher.createPublicKey(invalidPub);

            // tests that key that should be valid can encrypt
            assertNotNull(testObj.encryptText("tttest"));
            fail("Correct length but invalid key should throw exception");

        } catch (PublicKeyException e) {
            //Pass
        }
    }

    @Test
    void createPrivateKeyTest() throws PrivateKeyException, NoSuchPaddingException, NoSuchAlgorithmException {

        // Invalid  length modulus & exponent tests
        try {
            invalidCipher.createPrivateKey("32424352346544445", privExp);
            fail("Invalid private Modulus, should throw exception");
        } catch (Exception e) {
            //Pass
        }
        try {
            testObj.createPrivateKey(privMod, "4343533344344334");
            fail("Invalid Private key modulus or exponent");
        } catch (Exception e) {
            //Pass
        }

        try {
            testObj.createPrivateKey(privMod,privExp);
        } catch (PrivateKeyException e) {
            fail("valid key, exception not expected");
        }

    }

    @Test
    void getCipherEncryptTest() throws Exception {
        testObj.genKeyPair("RSA");

        try {
            invalidCipher.getCipherEncrypt();
        } catch (Exception e) {

        }

        try {
            testObj.getCipherEncrypt();
        } catch (Exception e) {
            fail("valid cipher should be active");
        }


        Cipher cipher = testObj.getCipherEncrypt();
        SealedObject sealedObject = new SealedObject("tt", cipher);
        assertTrue(sealedObject instanceof SealedObject);
    }


    @Test
    void getCipherDecryptTest() throws Exception {
        testObj.createPrivateKey("Invalid Key to set key to null", "test.test");
        try {
            invalidCipher.getCipherDecrypt();
        } catch (Exception e) {
        }
        try {
            testObj.getCipherDecrypt();
        } catch (Exception e) {
            fail("Should have valid private key");
        }

        testObj.genKeyPair("RSA");
        SealedObject test = testObj.encryptText("test");
        Cipher cipher = testObj.getCipherDecrypt();
        assertEquals(test.getObject(cipher), "test");
    }

    @Test
    void getterSetterTests() {
        testObj.genKeyPair("RSA");
        SealedObject obj = testObj.encryptText("test");
        int before = testObj.getEncryptedMsgs().size();
        testObj.removeEncryption(0);
        assertEquals(testObj.getEncryptedMsgs().size(), 0);

        String st0 = "SunRsaSign RSA private CRT key, 2048 bits\n  params: null\n  modulus: 24828996546712522177923682382870631806393534613051997600612369916221219798735258541982955008717245418599289363405192528138182156032933279621187609155604359467670256020683156115006973798715513246056740139559764700688041802748247581098476496820467920620444457814948490728202577048208542897968076616145048546114514816321165748337706585068532250522157725993115027978053586854481006825039159079650936516669460223529880023833236690718360087759403997650313248061758040622167616110539284795549700152099077337421471249473894856473517889230833262474150551934620341023272716324300968692615294570141874512888508821374774253040803\n  private exponent: 4665216037905580025008044690215740117245677788503018118832731482190947107765475589147780764718314235998468791994927152471025147159460158464002078507440256381660612060953238390530477064213846073392781147726306399808849150236384343403674864303328531585518913797294287422785396551133558100700045706260740158946095338218260092832866727575854316861566354685789580246242768635390471946925143557140550396080379852160163842734020429059119890480799654350213622786347473232030388463916215509226831224485019790847574988698306880451145553493509979318878248558166426337086337137014705790421698783255667159941298691256018693798513";
        String st1 = "Sun RSA private key, 2048 bits\n  params: null\n  modulus: 16541376069650746008605377437192115834982416547062948976201818285541912793944130874370020824023669261025218058008578102192954084121189754078851774672956411118354966513635890840158488928573282867612496498392493296074961164171005593482604232135087751499947778405670583118144862792323721725509847497098073572149552828463416533570928136379081250526579398964002049999050472599423394295314738980213028818726664037519357490475906723228411680513323961612883942469503148845378094600295387094625500824328407494788676144405755535005397696964382609931567580372418191750966630232612493844745829999024970334743052446724759198482683\n  private exponent: 7442950377922865694916825232240693102488616835624750940067678096683483625747569698861674231039473623119347167441520967630010879146890529899617925497517610013720030021523853471862210354680524566016523631553110987807719757850356042330416653210119515754184049276055050209362251230340635518311181360787419191740704722751202749153532646026154395618061345388606969955017557297672333187009838339513438307676043094062772145145183605371315859936179213467685465272028197905822997342868579899318357020482094892785358447752506942120785344708610400433222985191398979475275304552488461955592240590302894966659368669881845987526729";
        String st2 = "SunRsaSign RSA private CRT key, 2048 bits\n  params: null\n  modulus: 17446076252604900776812737535674148656241492113082623290841973805123217865756271871972721173886480086900154388225988074258829093174314247417619916631985237270228502660715784199401819981671548092334203130460915842326327795410072146447013676639264709475534973337545203835862486069584847405049703277013905326863751405237425894498833951030556674645223321524460714070866286100861655562072086325057306899826797433094460099823588542668370921218517896995014484287826704688794638004599956413721364071200481403336033964531909715991896563724885902257133268578124930761794391673954481442911014150973610658632496975137111541553587\n  private exponent: 7182660626024771240062274504288567969890412136582317494445857106822629422369588593459693954770663986827898220899852147941647109144900545093950296634942485497128423009772087359327706589338262517774104229154468945140134223045830090718728703222311677236811032708144173366178518991249803205564049221361813838433718529007359836496473419670337694696266481408640242936277669480316397088601236089955350235880969360701210671510111328395246495721544460686229495860229619502969209404488408085891799192918949003197321238485176921597546843940850249024437417937197761981393587759052628971840856105207640778581158659939084028485809";
        String st3 = "Sun RSA private key, 2048 bits\\n  params: null\\n  modulus: 28159084925863920559201728940336475104241550383379277641579510941224220586815506304075090549177545092628472796861535965434106013957153714600504678180224258625234535766516116210146074403482169486102489697149378772392876484063582647927020130718630907507186328437895387969664733770533222799085542163618666411790857567612686047283419259341006736040651158121825959570451015396514902886897684297782872296695062105633752621981554339707283497670560928850134882958118100116997974668947738642966981019335987667992783670834098118562319604664536541079497722228884636961548864881537239824691730230750449291602737793099096734093493\\n  private exponent: 7801033704838264517339313527331874238866740753477183344683424640262248027438276583560238399848444522968133300270351816354450597210828735558337472527582154196862203509114951949454691653106218917400503577237959030647803003565289936307169652155690110269015593925849957489314935171548914249053162389530501966407811084668366357726191757947936215558485298812553636781158782487326856853146357924811006029030622679522435961612438477678533974537583060865368554632287366416589835011042798273751894957600399212363723589172501120302839971319711390617917766296037063834863529022150557185344201545931046507513052527374982683593473";
        String privMod = "18559209039457539157136515599884134678120354450879745517720250441672621351277056066172101901048601880346375778740402711598838483663733316433702360026869231030218653071301151770520665956766231697531690546018268384435479673656966905423668063010589404845937786123691411990703639295569983982999694105131143651042539294879654051266916762665224428275200820970132671787756771975358844216514908817499997367821384366530149871453513019326712003758980662560649113913815902470947684525836880333838460113794712335047306781211030406044161824366957282300368522312715695987737145731381010536761171631344691683025781727016286617294733";
        String privExp = "13730592024156403917984056264033784793373872864265305723060012861664994107725702128941216912190709229450148404840123378772010966613054817593183890447575896603921168390605423573637258488789042984673109050067011930478030234172020345398354665351350197344403001327392187646386875427392255419072022954688017929786765000087347840386163113554815615447052577723167177895509653487587752220996361424973060745762710697266283001138810945811910500039857643791032641804818274208900714242637562345468854715688243647518808403421253959991842434132479058263463136650749566373498295888221179403561422224272409738666366777012127726533737";
        System.out.println(privMod.length());
        System.out.println(privExp.length());
        System.out.println(st0.length());
        System.out.println(st1.length());
        System.out.println(st2.length());
        System.out.println(st3.length());
        System.out.println("break");
        System.out.println(st1.substring(57, 674));
        System.out.println(st1.substring(695, 1311));
        System.out.println(st0.substring(68, 685));
        System.out.println(st0.substring(706, 1322));
    }
}




















