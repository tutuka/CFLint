component extends = 'base'  {		
		property name = 'reporter' inject = 'components.error.Errorfunctions';
        public function init() {
			var config = new components.configuration.Manager('VoucherEngine', Attributes);
			server.di = createObject('component', 'components.XmlRpc').init();
			exporter = server.di.getInstance('components.eftBatchProcessor.service.SecExporter');
            return this;
        }
}