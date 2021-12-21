package it.unibo.giacche.contextaware.utils

object PrivacyConfiguration {
    var REQ_ID = ""
    var GPS_PERT_ENABLED = Constants.DEFAULT_GPS_PERT_ENABLED
    var GPS_PERT_REAL_DECIMALS = Constants.DEFAULT_GPS_PERT_REAL_DECIMALS
    var DUMMY_UPDATES_ENABLED = Constants.DEFAULT_DUMMY_UPDATES_ENABLED
    var D_UP_MIN: Double = Constants.DEFAULT_D_UP_MIN
    var D_UP_RANGE: Double = Constants.DEFAULT_D_UP_RANGE
    var D_UP_COUNT = Constants.DEFAULT_D_UP_COUNT
    var SPATIAL_CLOAKING_ENABLED = Constants.DEFAULT_SPATIAL_CLOAKING_ENABLED
    var SP_CL_TIMEOUT = Constants.DEFAULT_SP_CL_TIMEOUT
    var SP_CL_K = Constants.DEFAULT_SP_CL_K
    var SP_CL_RANGEX: Double = Constants.DEFAULT_SP_CL_RANGE
    var SP_CL_RANGEY: Double = Constants.DEFAULT_SP_CL_RANGE
    var ALPHA_ENABLED = Constants.DEFAULT_ALPHA_ENABLED
    var ALPHA_VALUE = Constants.DEFAULT_ALPHA_VALUE
}