import numpy;

def file2matrix(filename) :
    fr = open(filename)
    lines = fr.readlines()
    numberOfLines = len(lines)
    fr.close()
    returnMat  = numpy.zeros((numberOfLines, 3))
    classLabelVetctor = []
    index = 0
    for line in lines :
        line = line.strip()
        listFromLine = line.split('\t');
        returnMat[index, : ] = listFromLine[0:3];
        classLabelVetctor.append(listFromLine[-1])
        index += 1
    return returnMat, classLabelVetctor


if __name__ == '__main__' :
    datingDataMat, datingLabels = file2matrix("/Users/songjianzhang/Code/machinelearninginaction/Ch02/datingTestSet.txt")
    print(datingDataMat)
    print(datingLabels)



