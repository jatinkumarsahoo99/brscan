package vraj.app.cancerscreening.screening.data.constant

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class AppConstant {
    companion object {
        const val APP_PREF = "cancer_screening_pref"
        const val PREF_USER_ID = "pref_user_id"
        const val PREF_PATIENT_ID = "pref_patient_id"
        const val INTENT_REPORT_PATH = "report_path"

        var strPatientName = ""
        var strPatientGender = ""
        var strPatientAge = ""
        var strPatientWeight = ""
        var strPatientHeight = ""
        var strSaveReportPath = ""
        var strHospitalLogoPath = ""
        var strUserId = ""
        var strPatientId = ""
        var strHospitalDocName = ""
        var strHospitalDocSpecialization = ""
        var strHospitalDocDesignation = ""


        val KEY_PATIENT_NAME = "patient_name"
        val KEY_PATIENT_GENDER = "patient_gender"
        var KEY_PATIENT_AGE = "patient_age"
        var KEY_PATIENT_WEIGHT = "patient_weight"
        var KEY_PATIENT_HEIGHT = "patient_height"
        var KEY_SAVE_PATH = "save_path"
        var KEY_HOSPITAL_LOGO = "hospital_logo_path"
        var KEY_USER_ID = "user_id"
        var KEY_PATIENT_ID = "patient_id"
        var KEY_HOSPITAL_DOC_NAME = "hospital_doc_name"
        var KEY_HOSPITAL_DOC_SPECIALIZATION = "hospital_doc_specialization"
        var KEY_HOSPITAL_DOC_DESIGNATION = "hospital_doc_designation"

        const val REQUEST_PERMISSIONS_CODE = 100

        const val PAGE_WIDTH_A4 = 1190
        const val PAGE_HEIGHT_A4 = 1684
        const val PAGE_PADDING_TOTAL = 100
        const val PAGE_PADDING_LEFT = 50
        const val PAGE_PADDING_RIGHT = 50
        const val PAGE_PADDING_TOP = 30
        const val PAGE_FOOTER_BOTTOM = 20
        const val LINE_MARGIN = 10
        const val BLUETOOTHDEVICENAME = "BR-SCAN-"

        @RequiresApi(Build.VERSION_CODES.S)
        val REQUEST_BLUETOOTH_PERMISSION: MutableList<String> = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT)

    }



}