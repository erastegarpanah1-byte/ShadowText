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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onBack: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val isPersian = Locale.getDefault().language == "fa"
    Scaffold(containerColor=c.background, topBar={TopAppBar(title={Text(stringResource(R.string.terms_title), fontWeight=FontWeight.SemiBold, color=c.onBackground)}, navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint=c.onBackground)}}, colors=TopAppBarDefaults.topAppBarColors(containerColor=c.background))}){padding->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal=24.dp, vertical=8.dp)){if(isPersian) TermsContentFa() else TermsContentEn(); Spacer(Modifier.height(40.dp))}
    }
}

@Composable private fun TermsContentEn(){val c=MaterialTheme.colorScheme
SectionTitle("About ShadowText", c.primary)
BodyText("ShadowText is a completely offline steganography tool designed for educational, research, and legitimate purposes in the field of data hiding and retrieval.", c.onSurfaceVariant)
BodyText("This software allows users to hide data in supported formats within other content, and to extract hidden data where it exists.", c.onSurfaceVariant)
BodyText("ShadowText is not an online service, social network, messaging platform, or cloud storage system.", c.onSurfaceVariant)
SectionTitle("Privacy by Design", c.primary)
BodyText("ShadowText is designed for local processing.", c.onSurfaceVariant)
BoldText("There is no central server for processing user data, and no data entered for steganography operations is sent to any server.", c.onBackground)
BodyText("By default, ShadowText does not send personal information, user files, hidden messages, or processed content to the developer.", c.onSurfaceVariant)
BodyText("Data entered by the user remains with the user, who is responsible for its storage, transmission, and use.", c.onSurfaceVariant)
SectionTitle("User Responsibility", c.primary)
BodyText("The steganography capabilities of ShadowText are technical tools and do not grant permission for unauthorized use.", c.onSurfaceVariant)
BodyText("Before hiding, extracting, modifying, or processing any data, the user must ensure that:", c.onSurfaceVariant)
Bullet("They own the data; or", c.onSurfaceVariant)
Bullet("They have legal and valid authorization to use and process it; and", c.onSurfaceVariant)
Bullet("Their use of the software complies with applicable laws and regulations.", c.onSurfaceVariant)
BoldText("The user bears full responsibility for how ShadowText is used and for the data processed with it.", c.onBackground)
BodyText("The developer cannot and should not verify or control the content of data processed locally by the user within the application.", c.onSurfaceVariant)
SectionTitle("Unauthorized Use Is Prohibited", c.primary)
BodyText("Using ShadowText for illegal or unauthorized activities is prohibited, including:", c.onSurfaceVariant)
Bullet("Unauthorized concealment of data belonging to others;", c.onSurfaceVariant)
Bullet("Unauthorized prevention of access by authorized persons to data;", c.onSurfaceVariant)
Bullet("Modification or concealment of information with intent to commit a crime;", c.onSurfaceVariant)
Bullet("Unauthorized manipulation of data, computer systems, or telecommunications systems;", c.onSurfaceVariant)
Bullet("Violation of individuals' privacy;", c.onSurfaceVariant)
Bullet("Circumventing security controls;", c.onSurfaceVariant)
Bullet("Concealing or facilitating illegal activities.", c.onSurfaceVariant)
BodyText("These examples are illustrative and not exhaustive.", c.onSurfaceVariant)
BoldText("The existence of steganography capabilities in ShadowText does not in any way imply that such uses are permitted.", c.onBackground)
SectionTitle("Legal Warning", c.primary)
BodyText("Users should be aware that unauthorized use of methods such as data hiding, password changes, or data encryption, if carried out with the intent to prevent authorized persons from accessing data, computer systems, or telecommunications systems, may be subject to criminal laws and computer crime statutes.", c.onSurfaceVariant)
BodyText("Among other legal provisions, the following is referenced:", c.onSurfaceVariant)
QuoteText("Anyone who, without authorization, prevents authorized persons from accessing data, computer systems, or telecommunications systems by acts such as concealing data, changing passwords, or encrypting data shall be sentenced to imprisonment from ninety-one days to one year or a fine of 20,000,000 to 80,000,000 Rials, or both.", c.onSurfaceVariant)
BodyText("This warning is provided solely for the user's awareness and does not replace the official text of laws, judicial interpretation, or legal advice.", c.onSurfaceVariant)
BodyText("The user is obligated to comply with the applicable laws in their jurisdiction.", c.onSurfaceVariant)
SectionTitle("No Liability for Misuse", c.primary)
BodyText("ShadowText is a general-purpose data processing and concealment tool.", c.onSurfaceVariant)
BodyText("The developer does not observe or inspect the data processed on the device, and in offline use has no direct control over input, output, or transmission.", c.onSurfaceVariant)
BodyText("Therefore:", c.onSurfaceVariant)
BoldText("Responsibility for data selection, ownership, processing authorization, content, output usage, and transmission rests with the user.", c.onBackground)
BodyText("No part of ShadowText should be interpreted as advice, encouragement, or permission to engage in illegal activity.", c.onSurfaceVariant)
SectionTitle("Educational Purpose", c.primary)
BodyText("ShadowText has been developed for education and research in:", c.onSurfaceVariant)
Bullet("Steganography", c.onSurfaceVariant)
Bullet("Data Encoding", c.onSurfaceVariant)
Bullet("Data Extraction", c.onSurfaceVariant)
Bullet("Digital Privacy", c.onSurfaceVariant)
Bullet("Information Security", c.onSurfaceVariant)
Bullet("Secure Data Handling", c.onSurfaceVariant)
BodyText("These capabilities are provided for study and experimentation of data processing and concealment technologies.", c.onSurfaceVariant)
SectionTitle("Acceptance of Terms", c.primary)
BodyText("By using ShadowText, the user confirms that:", c.onSurfaceVariant)
BodyText("1. ShadowText is an offline steganography tool.", c.onSurfaceVariant)
BodyText("2. Data processing occurs in the local environment of the device.", c.onSurfaceVariant)
BodyText("3. The user is responsible for the data they enter or process.", c.onSurfaceVariant)
BodyText("4. The user only processes data they have legal permission to use.", c.onSurfaceVariant)
BodyText("5. The user will not use ShadowText for illegal or unauthorized activities.", c.onSurfaceVariant)
BodyText("6. The user is responsible for complying with laws and regulations in their jurisdiction.", c.onSurfaceVariant)
BodyText("7. The developer has not issued any license for illegal use of ShadowText's capabilities.", c.onSurfaceVariant)
Spacer(Modifier.height(16.dp))
BodyText("This software is an educational and technical tool for steganography.", c.onSurfaceVariant)
BoldText("Technology is neutral; how it is used is the responsibility of the user.", c.onBackground)}

@Composable private fun TermsContentFa(){val c=MaterialTheme.colorScheme
SectionTitle("درباره ShadowText", c.primary)
BoldText("ShadowText یک ابزار استگانوگرافی کاملاً آفلاین است که برای اهداف آموزشی، پژوهشی و استفاده‌های مشروع در زمینه مخفی‌سازی و بازیابی داده طراحی شده است.", c.onBackground)
BodyText("این نرم‌افزار به کاربران امکان می‌دهد داده‌هایی را در قالب‌های پشتیبانی‌شده درون محتوای دیگری مخفی کرده و در صورت وجود داده مخفی، آن را مجدداً استخراج کنند.", c.onSurfaceVariant)
BodyText("ShadowText یک سرویس آنلاین، شبکه اجتماعی، پیام‌رسان یا سامانه ذخیره‌سازی ابری نیست.", c.onSurfaceVariant)
SectionTitle("Privacy by Design", c.primary)
BodyText("ShadowText برای پردازش محلی طراحی شده است.", c.onSurfaceVariant)
BoldText("هیچ سرور مرکزی برای پردازش داده‌های کاربر وجود ندارد و داده‌های واردشده برای عملیات استگانوگرافی به سرور ارسال نمی‌شوند.", c.onBackground)
BodyText("ShadowText به طور پیش‌فرض اطلاعات شخصی، فایل‌های کاربر، پیام‌های مخفی یا محتوای پردازش‌شده را برای توسعه‌دهنده ارسال نمی‌کند.", c.onSurfaceVariant)
BodyText("داده‌ای که کاربر وارد برنامه می‌کند، در اختیار خود کاربر است و کاربر مسئول نگهداری، انتقال و استفاده از آن است.", c.onSurfaceVariant)
SectionTitle("مسئولیت کاربر", c.primary)
BodyText("قابلیت‌های استگانوگرافی ShadowText یک ابزار فنی هستند و مجوزی برای استفاده غیرمجاز از آن‌ها ایجاد نمی‌کنند.", c.onSurfaceVariant)
BodyText("کاربر موظف است پیش از مخفی‌سازی، استخراج، تغییر یا پردازش هرگونه داده، اطمینان حاصل کند که:", c.onSurfaceVariant)
Bullet("مالک داده است؛ یا", c.onSurfaceVariant)
Bullet("اجازه قانونی و معتبر برای استفاده و پردازش آن را دارد؛ و", c.onSurfaceVariant)
Bullet("استفاده از نرم‌افزار با قوانین و مقررات محل استفاده مطابقت دارد.", c.onSurfaceVariant)
BoldText("کاربر مسئول کامل نحوه استفاده از ShadowText و داده‌هایی است که با استفاده از آن پردازش می‌کند.", c.onBackground)
BodyText("توسعه‌دهنده نمی‌تواند و نباید محتوای داده‌ای را که کاربر به صورت محلی در برنامه پردازش می‌کند تأیید یا کنترل کند.", c.onSurfaceVariant)
SectionTitle("استفاده غیرمجاز ممنوع است", c.primary)
BodyText("استفاده از ShadowText برای فعالیت‌های غیرقانونی یا بدون مجوز ممنوع است.", c.onSurfaceVariant)
BodyText("از جمله استفاده برای:", c.onSurfaceVariant)
Bullet("مخفی کردن غیرمجاز داده‌های متعلق به دیگران؛", c.onSurfaceVariant)
Bullet("جلوگیری غیرمجاز از دسترسی افراد مجاز به داده‌ها؛", c.onSurfaceVariant)
Bullet("تغییر یا مخفی‌سازی اطلاعات با هدف ارتکاب جرم؛", c.onSurfaceVariant)
Bullet("دستکاری غیرمجاز داده‌ها یا سامانه‌های رایانه‌ای و مخابراتی؛", c.onSurfaceVariant)
Bullet("نقض حریم خصوصی اشخاص؛", c.onSurfaceVariant)
Bullet("دور زدن کنترل‌های امنیتی؛", c.onSurfaceVariant)
Bullet("پنهان‌سازی یا تسهیل فعالیت‌های غیرقانونی.", c.onSurfaceVariant)
BodyText("این موارد صرفاً نمونه هستند و محدود به موارد فوق نیستند.", c.onSurfaceVariant)
BoldText("وجود قابلیت استگانوگرافی در ShadowText به هیچ عنوان به معنای مجاز بودن چنین استفاده‌هایی نیست.", c.onBackground)
SectionTitle("هشدار حقوقی", c.primary)
BodyText("کاربر باید توجه داشته باشد که استفاده غیرمجاز از روش‌هایی مانند مخفی‌کردن داده، تغییر گذرواژه یا رمزنگاری داده‌ها، در صورتی که با هدف جلوگیری از دسترسی اشخاص مجاز به داده‌ها یا سامانه‌های رایانه‌ای یا مخابراتی انجام شود، می‌تواند حسب مورد مشمول قوانین و مقررات کیفری و جرایم رایانه‌ای باشد.", c.onSurfaceVariant)
BodyText("از جمله، در متن قانونی مورد استناد این برنامه آمده است:", c.onSurfaceVariant)
QuoteText("هر کس به طور غیرمجاز با اعمالی از قبیل مخفی کردن داده‌ها، تغییر گذرواژه یا رمزنگاری داده‌ها مانع دسترسی اشخاص مجاز به داده‌ها یا سامانه‌های رایانه‌ای یا مخابراتی شود، به حبس از نود و یک روز تا یک سال یا جزای نقدی از ۲۰,۰۰۰,۰۰۰ تا ۸۰,۰۰۰,۰۰۰ ریال یا هر دو مجازات محکوم خواهد شد.", c.onSurfaceVariant)
BodyText("این هشدار صرفاً جهت آگاهی کاربر ارائه شده و جایگزین متن رسمی قوانین، تفسیر قضایی یا مشاوره حقوقی نیست.", c.onSurfaceVariant)
BodyText("کاربر موظف است قوانین لازم‌الاجرا در محل استفاده خود را رعایت کند.", c.onSurfaceVariant)
SectionTitle("عدم مسئولیت در قبال سوءاستفاده", c.primary)
BodyText("ShadowText یک ابزار عمومی پردازش و مخفی‌سازی داده است.", c.onSurfaceVariant)
BodyText("توسعه‌دهنده داده‌هایی را که کاربر در دستگاه خود پردازش می‌کند مشاهده یا بررسی نمی‌کند و کنترل مستقیمی بر محتوای ورودی، خروجی یا نحوه انتقال آن ندارد.", c.onSurfaceVariant)
BodyText("بنابراین:", c.onSurfaceVariant)
BoldText("مسئولیت انتخاب داده، مالکیت داده، مجوز پردازش داده، محتوای داده، نحوه استفاده از خروجی و نحوه انتقال یا انتشار آن، بر عهده کاربر است.", c.onBackground)
BodyText("هیچ بخشی از ShadowText نباید به عنوان توصیه، تشویق یا مجوز انجام فعالیت غیرقانونی تفسیر شود.", c.onSurfaceVariant)
SectionTitle("ماهیت آموزشی", c.primary)
BodyText("ShadowText با هدف آموزش و پژوهش در زمینه‌های زیر توسعه یافته است:", c.onSurfaceVariant)
Bullet("Steganography", c.onSurfaceVariant)
Bullet("Data Encoding", c.onSurfaceVariant)
Bullet("Data Extraction", c.onSurfaceVariant)
Bullet("Digital Privacy", c.onSurfaceVariant)
Bullet("Information Security", c.onSurfaceVariant)
Bullet("Secure Data Handling", c.onSurfaceVariant)
BodyText("این قابلیت‌ها برای مطالعه و آزمایش فناوری‌های پردازش و مخفی‌سازی داده ارائه شده‌اند.", c.onSurfaceVariant)
SectionTitle("پذیرش شرایط", c.primary)
BodyText("با استفاده از ShadowText، کاربر تأیید می‌کند که:", c.onSurfaceVariant)
BodyText("۱. ShadowText یک ابزار استگانوگرافی آفلاین است.", c.onSurfaceVariant)
BodyText("۲. پردازش داده‌ها در محیط محلی دستگاه انجام می‌شود.", c.onSurfaceVariant)
BodyText("۳. کاربر مسئول داده‌هایی است که وارد یا پردازش می‌کند.", c.onSurfaceVariant)
BodyText("۴. کاربر فقط داده‌هایی را پردازش می‌کند که مجوز قانونی استفاده از آن‌ها را دارد.", c.onSurfaceVariant)
BodyText("۵. کاربر از ShadowText برای فعالیت‌های غیرقانونی یا غیرمجاز استفاده نخواهد کرد.", c.onSurfaceVariant)
BodyText("۶. کاربر مسئول رعایت قوانین و مقررات محل استفاده از نرم‌افزار است.", c.onSurfaceVariant)
BodyText("۷. توسعه‌دهنده مجوزی برای استفاده غیرقانونی از قابلیت‌های ShadowText صادر نکرده است.", c.onSurfaceVariant)
Spacer(Modifier.height(16.dp))
BodyText("این نرم‌افزار یک ابزار آموزشی و فنی برای استگانوگرافی است.", c.onSurfaceVariant)
BoldText("فناوری خنثی است؛ نحوه استفاده از آن بر عهده کاربر است.", c.onBackground)}

@Composable private fun SectionTitle(text:String,color:Color){Spacer(Modifier.height(24.dp));Text(text,style=MaterialTheme.typography.titleMedium.copy(fontWeight=FontWeight.Bold),color=color);Spacer(Modifier.height(8.dp))}
@Composable private fun BodyText(text:String,color:Color){Text(text,style=MaterialTheme.typography.bodyMedium,color=color,modifier=Modifier.padding(vertical=2.dp))}
@Composable private fun BoldText(text:String,color:Color){Text(text,style=MaterialTheme.typography.bodyMedium.copy(fontWeight=FontWeight.SemiBold),color=color,modifier=Modifier.padding(vertical=4.dp))}
@Composable private fun Bullet(text:String,color:Color){Row(Modifier.padding(start=16.dp,top=2.dp,bottom=2.dp)){Text("\u2022",color=MaterialTheme.colorScheme.primary,modifier=Modifier.padding(end=8.dp));Text(text,style=MaterialTheme.typography.bodyMedium,color=color)}}
@Composable private fun QuoteText(text:String,color:Color){Spacer(Modifier.height(4.dp));Surface(Modifier.fillMaxWidth().padding(vertical=4.dp),shape=RoundedCornerShape(10.dp),color=MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)){Text(text,style=MaterialTheme.typography.bodyMedium.copy(fontStyle=FontStyle.Italic,fontSize=13.sp),color=color,modifier=Modifier.padding(16.dp))};Spacer(Modifier.height(4.dp))}
