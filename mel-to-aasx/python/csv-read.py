import csv # Import all PYI40AAS classes from the model package
filename = 'MEL-UseCase-7items.csv'
csv.register_dialect('mel_csv', delimiter=';', quoting=csv.QUOTE_NONE)

with open(filename, newline='') as f:
    reader = csv.DictReader(f, dialect='mel_csv')
    try:
        for row in reader:
            print(row)
    except csv.Error as e:
        sys.exit('file {}, line {}: {}'.format(filename, reader.line_num, e))
