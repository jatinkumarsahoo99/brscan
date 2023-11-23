package vraj.app.cancerscreening.screening.ui.activities

import android.os.Bundle
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import vraj.app.cancerscreening.screening.ui.fragments.BluetoothFragment
import vraj.app.cancerscreening.screening.R

class DemoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)


        AppConstant.strPatientName = intent.getStringExtra(AppConstant.KEY_PATIENT_NAME)!!
        AppConstant.strPatientGender = intent.getStringExtra(AppConstant.KEY_PATIENT_GENDER)!!
        AppConstant.strPatientWeight = intent.getStringExtra(AppConstant.KEY_PATIENT_WEIGHT)!!
        AppConstant.strPatientHeight = intent.getStringExtra(AppConstant.KEY_PATIENT_HEIGHT)!!
        AppConstant.strSaveReportPath = intent.getStringExtra(AppConstant.KEY_SAVE_PATH)!!
        AppConstant.strHospitalLogoPath = intent.getStringExtra(AppConstant.KEY_HOSPITAL_LOGO)!!
        AppConstant.strUserId = intent.getStringExtra(AppConstant.KEY_USER_ID)!!
        AppConstant.strPatientId = intent.getStringExtra(AppConstant.KEY_PATIENT_ID)!!
        AppConstant.strHospitalDocName = intent.getStringExtra(AppConstant.KEY_HOSPITAL_DOC_NAME)!!
        AppConstant.strHospitalDocSpecialization = intent.getStringExtra(AppConstant.KEY_HOSPITAL_DOC_SPECIALIZATION)!!
        AppConstant.strHospitalDocDesignation = intent.getStringExtra(AppConstant.KEY_HOSPITAL_DOC_DESIGNATION)!!
        replaceFragmenty(BluetoothFragment(), false, R.id.fragment_loader)
    }
}