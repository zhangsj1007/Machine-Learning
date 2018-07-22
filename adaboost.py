import numpy;

def loadSimpleData() :
    dataMat = numpy.matrix([[1. , 2.1],
                            [2., 1.1],
                            [1.3, 1.],
                            [1., 1.],
                            [2., 1.]])
    classLabels = [1.0, 1.0, -1.0, -1.0, 1.0]
    return dataMat, classLabels

def stumpClassify(dataMatrix, dimen, threshVal, threshIneq) :
    retArray = numpy.ones(numpy.shape(dataMatrix)[0], 1)
    if threshIneq == 'lt' :
        retArray[dataMatrix[:, dimen] <= threshVal] = -1.0
    else :
        retArray[dataMatrix[:, dimen] > threshVal] = 1.0
    return retArray

def buildStump(dataArr, classLabels, D) :
    dataMatrix = numpy.mat(dataArr); labelMat = numpy.mat(classLabels).T
    m, n = numpy.shape(dataMatrix)
    numSteps = 10.0; bestStump = {}; bestClassEst = numpy.mat(numpy.zeros(m, 1))
    minError = numpy.inf
    for i in range(n) :
        rangeMin = dataMatrix[:, i].min(); rangeMax = dataMatrix[:, i].max()
        stepSize = (rangeMax - rangeMin) / numSteps
        for j in range(-1, int(numSteps) + 1) :
            for inequal in ['lt', 'gt'] :
                threshVal = (rangeMin + float(j) * stepSize)




