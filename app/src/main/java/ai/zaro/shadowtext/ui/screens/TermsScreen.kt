package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onBack: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val isPersian = Locale.getDefault().language == "fa"

    Scaffold(
        containerColor = c.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.terms_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = c.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            if (isPersian) TermsContentFa() else TermsContentEn()
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TermsContentEn() {
    val c = MaterialTheme.colorScheme

    SectionTitle("About ShadowText", c)
    BodyText("ShadowText is a completely offline steganography tool designed for educational, research, and legitimate purposes in the field of data hiding and retrieval.", c)
    BodyText("This software allows users to hide data in supported formats within other content, and to extract hidden data where it exists.", c)
    BodyText("ShadowText is not an online service, social network, messaging platform, or cloud storage system.", c)

    SectionTitle("Privacy by Design", c)
    BodyText("ShadowText is designed for local processing.", c)
    BoldText("There is no central server for processing user data, and no data entered for steganography operations is sent to any server.", c)
    BodyText("By default, ShadowText does not send personal information, user files, hidden messages, or processed content to the developer.", c)
    BodyText("Data entered by the user remains with the user, who is responsible for its storage, transmission, and use.", c)

    SectionTitle("User Responsibility", c)
    BodyText("The steganography capabilities of ShadowText are technical tools and do not grant permission for unauthorized use.", c)
    BodyText("Before hiding, extracting, modifying, or processing any data, the user must ensure that:", c)
    Bullet("They own the data; or", c)
    Bullet("They have legal and valid authorization to use and process it; and", c)
    Bullet("Their use of the software complies with applicable laws and regulations.", c)
    BoldText("The user bears full responsibility for how ShadowText is used and for the data processed with it.", c)
    BodyText("The developer cannot and should not verify or control the content of data processed locally by the user within the application.", c)

    SectionTitle("Unauthorized Use Is Prohibited", c)
    BodyText("Using ShadowText for illegal or unauthorized activities is prohibited, including:", c)
    Bullet("Unauthorized concealment of data belonging to others;", c)
    Bullet("Unauthorized prevention of access by authorized persons to data;", c)
    Bullet("Modification or concealment of information with intent to commit a crime;", c)
    Bullet("Unauthorized manipulation of data, computer systems, or telecommunications systems;", c)
    Bullet("Violation of individuals\u2019 privacy;", c)
    Bullet("Circumventing security controls;", c)
    Bullet("Concealing or facilitating illegal activities.", c)
    BodyText("These examples are illustrative and not exhaustive.", c)
    BoldText("The existence of steganography capabilities in ShadowText does not in any way imply that such uses are permitted.", c)

    SectionTitle("Legal Warning", c)
    BodyText("Users should be aware that unauthorized use of methods such as data hiding, password changes, or data encryption—if carried out with the intent to prevent authorized persons from accessing data, computer systems, or telecommunications systems—may, depending on the case, be subject to criminal laws and computer crime statutes.", c)
    BodyText("Among other legal provisions, the following is referenced:", c)
    QuoteText("\u2018Anyone who, without authorization, prevents authorized persons from accessing data, computer systems, or telecommunications systems by acts such as concealing data, changing passwords, or encrypting data shall be sentenced to imprisonment from ninety-one days to one year or a fine of 20,000,000 to 80,000,000 Rials, or both.\u2019", c)
    BodyText("This warning is provided solely for the user\u2019s awareness and does not replace the official text of laws, judicial interpretation, or legal advice.", c)
    BodyText("The user is obligated to comply with the applicable laws in their jurisdiction.", c)

    SectionTitle("No Liability for Misuse", c)
    BodyText("ShadowText is a general-purpose data processing and concealment tool.", c)
    BodyText("The developer does not observe or inspect the data that the user processes on their device, and in the offline use of the software, has no direct control over input content, output content, or how it is transmitted.", c)
    BodyText("Therefore:", c)
    BoldText("Responsibility for data selection, data ownership, data processing authorization, data content, how the output is used, and how it is transmitted or distributed rests with the user.", c)
    BodyText("No part of ShadowText should be interpreted as advice, encouragement, or permission to engage in illegal activity.", c)

    SectionTitle("Educational Purpose", c)
    BodyText("ShadowText has been developed for education and research in the following areas:", c)
    Bullet("Steganography", c)
    Bullet("Data Encoding", c)
    Bullet("Data Extraction", c)
    Bullet("Digital Privacy", c)
    Bullet("Information Security", c)
    Bullet("Secure Data Handling", c)
    BodyText("These capabilities are provided for the study and experimentation of data processing and concealment technologies.", c)

    SectionTitle("Acceptance of Terms", c)
    BodyText("By using ShadowText, the user confirms that:", c)
    BodyText("1. ShadowText is an offline steganography tool.")
    BodyText("2. Data processing occurs in the local environment of the device.")
    BodyText("3. The user is responsible for the data they enter or process.")
    BodyText("4. The user only processes data they have legal permission to use.")
    BodyText("5. The user will not use ShadowText for illegal or unauthorized activities.")
    BodyText("6. The user is responsible for complying with laws and regulations in their jurisdiction.")
    BodyText("7. The developer has not issued any license for the illegal use of ShadowText\u2019s capabilities.")

    Spacer(Modifier.height(16.dp))
    BodyText("This software is an educational and technical tool for steganography.")
    BoldText("Technology is neutral; how it is used is the responsibility of the user.", c)
}

@Composable
private fun TermsContentFa() {
    val c = MaterialTheme.colorScheme

    SectionTitle("درباره ShadowText", c)
    BoldText("ShadowText یک ابزار استگانوگرافی کاملاً آفلاین است که برای اهداف آموزشی، پژوهشی و استفاده‌های مشروع در زمینه مخفی‌سازی و بازیابی داده طراحی شده است.", c)
    BodyText("این نرم‌افزار به کاربران امکان می‌دهد داده‌هایی را در قالب‌های پشتیبانی‌شده درون محتوای دیگری مخفی کرده و در صورت وجود داده مخفی، آن را مجدداً استخراج کنند.")
    BodyText("ShadowText یک سرویس آنلاین، شبکه اجتماعی، پیام‌رسان یا سامانه ذخیره‌سازی ابری نیست.")

    SectionTitle("Privacy by Design", c)
    BodyText("ShadowText برای پردازش محلی طراحی شده است.")
    BoldText("هیچ سرور مرکزی برای پردازش داده‌های کاربر وجود ندارد و داده‌های واردشده برای عملیات استگانوگرافی به سرور ارسال نمی‌شوند.", c)
    BodyText("ShadowText به طور پیش‌فرض اطلاعات شخصی، فایل‌های کاربر، پیام‌های مخفی یا محتوای پردازش‌شده را برای توسعه‌دهنده ارسال نمی‌کند.")
    BodyText("داده‌ای که کاربر وارد برنامه می‌کند، در اختیار خود کاربر است و کاربر مسئول نگهداری، انتقال و استفاده از آن است.")

    SectionTitle("مسئولیت کاربر", c)
    BodyText("قابلیت‌های استگانوگرافی ShadowText یک ابزار فنی هستند و مجوزی برای استفاده غیرمجاز از آن‌ها ایجاد نمی‌کنند.")
    BodyText("کاربر موظف است پیش از مخفی‌سازی، استخراج، تغییر یا پردازش هرگونه داده، اطمینان حاصل کند که:")
    Bullet("مالک داده است؛ یا", c)
    Bullet("اجازه قانونی و معتبر برای استفاده و پردازش آن را دارد؛ و", c)
    Bullet("استفاده از نرم‌افزار با قوانین و مقررات محل استفاده مطابقت دارد.", c)
    BoldText("کاربر مسئول کامل نحوه استفاده از ShadowText و داده‌هایی است که با استفاده از آن پردازش می‌کند.", c)
    BodyText("توسعه‌دهنده نمی‌تواند و نباید محتوای داده‌ای را که کاربر به صورت محلی در برنامه پردازش می‌کند تأیید یا کنترل کند.")

    SectionTitle("استفاده غیرمجاز ممنوع است", c)
    BodyText("استفاده از ShadowText برای فعالیت‌های غیرقانونی یا بدون مجوز ممنوع است.")
    BodyText("از جمله استفاده برای:")
    Bullet("مخفی کردن غیرمجاز داده‌های متعلق به دیگران؛", c)
    Bullet("جلوگیری غیرمجاز از دسترسی افراد مجاز به داده‌ها؛", c)
    Bullet("تغییر یا مخفی‌سازی اطلاعات با هدف ارتکاب جرم؛", c)
    Bullet("دستکاری غیرمجاز داده‌ها یا سامانه‌های رایانه‌ای و مخابراتی؛", c)
    Bullet("نقض حریم خصوصی اشخاص؛", c)
    Bullet("دور زدن کنترل‌های امنیتی؛", c)
    Bullet("پنهان‌سازی یا تسهیل فعالیت‌های غیرقانونی.", c)
    BodyText("این موارد صرفاً نمونه هستند و محدود به موارد فوق نیستند.")
    BoldText("وجود قابلیت استگانوگرافی در ShadowText به هیچ عنوان به معنای مجاز بودن چنین استفاده‌هایی نیست.", c)

    SectionTitle("هشدار حقوقی", c)
    BodyText("کاربر باید توجه داشته باشد که استفاده غیرمجاز از روش‌هایی مانند مخفی‌کردن داده، تغییر گذرواژه یا رمزنگاری داده‌ها، در صورتی که با هدف جلوگیری از دسترسی اشخاص مجاز به داده‌ها یا سامانه‌های رایانه‌ای یا مخابراتی انجام شود، می‌تواند حسب مورد مشمول قوانین و مقررات کیفری و جرایم رایانه‌ای باشد.")
    BodyText("از جمله، در متن قانونی مورد استناد این برنامه آمده است:")
    QuoteText("«هر کس به طور غیرمجاز با اعمالی از قبیل مخفی کردن داده‌ها، تغییر گذرواژه یا رمزنگاری داده‌ها مانع دسترسی اشخاص مجاز به داده‌ها یا سامانه‌های رایانه‌ای یا مخابراتی شود، به حبس از نود و یک روز تا یک سال یا جزای نقدی از ۲۰,۰۰۰,۰۰۰ تا ۸۰,۰۰۰,۰۰۰ ریال یا هر دو مجازات محکوم خواهد شد.»", c)
    BodyText("این هشدار صرفاً جهت آگاهی کاربر ارائه شده و جایگزین متن رسمی قوانین، تفسیر قضایی یا مشاوره حقوقی نیست.")
    BodyText("کاربر موظف است قوانین لازم‌الاجرا در محل استفاده خود را رعایت کند.")

    SectionTitle("عدم مسئولیت در قبال سوءاستفاده", c)
    BodyText("ShadowText یک ابزار عمومی پردازش و مخفی‌سازی داده است.")
    BodyText("توسعه‌دهنده داده‌هایی را که کاربر در دستگاه خود پردازش می‌کند مشاهده یا بررسی نمی‌کند و در فرآیند استفاده آفلاین از نرم‌افزار کنترل مستقیمی بر محتوای ورودی، خروجی یا نحوه انتقال آن ندارد.")
    BodyText("بنابراین:")
    BoldText("مسئولیت انتخاب داده، مالکیت داده، مجوز پردازش داده، محتوای داده، نحوه استفاده از خروجی و نحوه انتقال یا انتشار آن، بر عهده کاربر است.", c)
    BodyText("هیچ بخشی از ShadowText نباید به عنوان توصیه، تشویق یا مجوز انجام فعالیت غیرقانونی تفسیر شود.")

    SectionTitle("ماهیت آموزشی", c)
    BodyText("ShadowText با هدف آموزش و پژوهش در زمینه‌های زیر توسعه یافته است:")
    Bullet("Steganography", c)
    Bullet("Data Encoding", c)
    Bullet("Data Extraction", c)
    Bullet("Digital Privacy", c)
    Bullet("Information Security", c)
    Bullet("Secure Data Handling", c)
    BodyText("این قابلیت‌ها برای مطالعه و آزمایش فناوری‌های پردازش و مخفی‌سازی داده ارائه شده‌اند.")

    SectionTitle("پذیرش شرایط", c)
    BodyText("با استفاده از ShadowText، کاربر تأیید می‌کند که:")
    BodyText("۱. ShadowText یک ابزار استگانوگرافی آفلاین است.")
    BodyText("۲. پردازش داده‌ها در محیط محلی دستگاه انجام می‌شود.")
    BodyText("۳. کاربر مسئول داده‌هایی است که وارد یا پردازش می‌کند.")
    BodyText("۴. کاربر فقط داده‌هایی را پردازش می‌کند که مجوز قانونی استفاده از آن‌ها را دارد.")
    BodyText("۵. کاربر از ShadowText برای فعالیت‌های غیرقانونی یا غیرمجاز استفاده نخواهد کرد.")
    BodyText("۶. کاربر مسئول رعایت قوانین و مقررات محل استفاده از نرم‌افزار است.")
    BodyText("۷. توسعه‌دهنده مجوزی برای استفاده غیرقانونی از قابلیت‌های ShadowText صادر نکرده است.")

    Spacer(Modifier.height(16.dp))
    BodyText("این نرم‌افزار یک ابزار آموزشی و فنی برای استگانوگرافی است.")
    BoldText("فناوری خنثی است؛ نحوه استفاده از آن بر عهده کاربر است.", c)
}

@Composable
private fun SectionTitle(text: String, c: androidx.compose.ui.graphics.Color) {
    Spacer(Modifier.height(24.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = c.primary
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun BodyText(text: String, c: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = c,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun BoldText(text: String, c: androidx.compose.ui.graphics.Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        color = c,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun Bullet(text: String, c: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(modifier = Modifier.padding(start = 16.dp, top = 2.dp, bottom = 2.dp)) {
        Text("\u2022", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = c)
    }
}

@Composable
private fun QuoteText(text: String, c: androidx.compose.ui.graphics.Color) {
    Spacer(Modifier.height(4.dp))
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 13.sp),
            color = c,
            modifier = Modifier.padding(16.dp)
        )
    }
    Spacer(Modifier.height(4.dp))
}
