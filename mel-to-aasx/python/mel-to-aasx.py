import csv # Import all PYI40AAS classes from the model package

import datetime
from pathlib import Path  # Used for easier handling of auxiliary file's local path

import pyecma376_2  # The base library for Open Packaging Specifications. We will use the OPCCoreProperties class.
from aas import model
from aas.adapter import aasx


csv.register_dialect('mel_csv', delimiter=';', quoting=csv.QUOTE_NONE)


infile = 'MEL-UseCase-7items.csv'
outfile = 'MEL-UseCase-7items.aasx'

object_store = model.DictObjectStore()
file_store = aasx.DictSupplementaryFileContainer()



with open(infile, newline='') as f:
    reader = csv.DictReader(f, dialect='mel_csv')
    try:
        for row in reader:
            submodel = model.Submodel(
                identification=model.Identifier(row['iri_id'] + '_SM', model.IdentifierType.IRI)
            )
            asset = model.Asset(
                kind=model.AssetKind.INSTANCE,  # define that the Asset is of kind instance
                identification=model.Identifier(id_=row['iri_id'], id_type=model.IdentifierType.IRI)
            )
            aas = model.AssetAdministrationShell(
                identification=model.Identifier(row['iri_id'] + '_AAS', model.IdentifierType.IRI),
                asset=model.AASReference.from_referable(asset),
                submodel={model.AASReference.from_referable(submodel)}
            )
            object_store.add(submodel)
            object_store.add(asset)
            object_store.add(aas)
#            print(row)
    except csv.Error as e:
        sys.exit('file {}, line {}: {}'.format(infile, reader.line_num, e))
